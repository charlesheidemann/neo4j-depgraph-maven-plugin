/*
 * Copyright (c) 2014 - 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.depgraph;

import static java.util.EnumSet.allOf;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;

import com.github.depgraph.dependency.DependencyNode;
import com.github.depgraph.dependency.DependencyNodeIdRenderer;
import com.github.depgraph.dependency.GraphFactory;
import com.github.depgraph.dependency.GraphStyleConfigurer;
import com.github.depgraph.dependency.MavenGraphAdapter;
import com.github.depgraph.dependency.NodeResolution;
import com.github.depgraph.dependency.SimpleGraphFactory;
import com.github.depgraph.graph.GraphBuilder;
import java.util.EnumSet;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.neo4j.driver.v1.AuthTokens;

/**
 * Creates a dependency graph of a maven module.
 */
@Mojo(
    name = "neo4j",
    defaultPhase = LifecyclePhase.NONE,
    requiresDependencyCollection = ResolutionScope.TEST,
    threadSafe = true)
public class Neo4jGraphMojo extends AbstractGraphMojo {

  /**
   * If set to {@code true}, the graph will additionally contain conflicting dependencies.<br/> The option {@link
   * #showAllAttributesForJson} does not enable this flag.
   *
   * @since 1.0.0
   */
  @Parameter(property = "showConflicts", defaultValue = "false")
  private boolean showConflicts;

  /**
   * If set to {@code true}, the graph will additionally contain duplicate dependencies.<br/> The option {@link
   * #showAllAttributesForJson} does not enable this flag.
   *
   * @since 1.0.0
   */
  @Parameter(property = "showDuplicates", defaultValue = "false")
  private boolean showDuplicates;

  /**
   * Merge dependencies with multiple types into one graph node instead of having a node per type.
   *
   * @since 3.0.0
   */
  @Parameter(property = "mergeTypes", defaultValue = "false")
  private boolean mergeTypes;

  /**
   * Merge dependencies with multiple classifiers into one graph node instead of having a node per classifier.
   *
   * @since 3.0.0
   */
  @Parameter(property = "mergeClassifiers", defaultValue = "false")
  private boolean mergeClassifiers;


  @Parameter(property = "neo4jUri", defaultValue = "bolt://localhost:7687")
  private String neo4jUri;

  @Parameter(property = "neo4jUser", defaultValue = "neo4j")
  private String neo4jUser;

  @Parameter(property = "neo4jPass", defaultValue = "test")
  private String neo4jPass;

  @Override
  protected GraphFactory createGraphFactory(ArtifactFilter globalFilter, ArtifactFilter transitiveIncludeExcludeFilter,
      ArtifactFilter targetFilter, GraphStyleConfigurer graphStyleConfigurer) {

    GraphBuilder<DependencyNode> graphBuilder = createGraphBuilder(graphStyleConfigurer);
    MavenGraphAdapter adapter = createMavenGraphAdapter(transitiveIncludeExcludeFilter, targetFilter);

    return new SimpleGraphFactory(adapter, globalFilter, graphBuilder);
  }

  GraphBuilder<DependencyNode> createGraphBuilder(GraphStyleConfigurer graphStyleConfigurer) {

    DependencyNodeIdRenderer nodeIdRenderer = DependencyNodeIdRenderer.versionlessId()
        .withClassifier(true)
        .withType(true);

    return graphStyleConfigurer
        .showGroupIds(true)
        .showArtifactIds(true)
        .showTypes(true)
        .showClassifiers(true)
        .showVersionsOnNodes(true)
        .showVersionsOnEdges(true)
        .configure(GraphBuilder.create(nodeIdRenderer)
            .neo4jUri(neo4jUri)
            .neo4AuthToken(AuthTokens.basic(neo4jUser, neo4jPass)));
  }


  private MavenGraphAdapter createMavenGraphAdapter(ArtifactFilter transitiveIncludeExcludeFilter,
      ArtifactFilter targetFilter) {
    MavenGraphAdapter adapter;
    if (requiresFullGraph()) {
      EnumSet<NodeResolution> resolutions = allOf(NodeResolution.class);
      resolutions = !this.showConflicts ? complementOf(of(NodeResolution.OMITTED_FOR_CONFLICT)) : resolutions;
      resolutions = !this.showDuplicates ? complementOf(of(NodeResolution.OMITTED_FOR_DUPLICATE)) : resolutions;

      adapter = new MavenGraphAdapter(this.dependenciesResolver, transitiveIncludeExcludeFilter, targetFilter,
          resolutions);
    } else {
      // there are no reachable paths to be omitted
      adapter = new MavenGraphAdapter(this.dependenciesResolver, transitiveIncludeExcludeFilter, targetFilter,
          EnumSet.of(
              NodeResolution.INCLUDED));
    }
    return adapter;
  }

  private boolean requiresFullGraph() {
    return this.showConflicts || this.showDuplicates;
  }
}
