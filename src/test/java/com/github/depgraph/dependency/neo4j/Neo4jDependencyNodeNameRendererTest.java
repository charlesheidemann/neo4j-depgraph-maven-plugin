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
package com.github.depgraph.dependency.neo4j;

import com.github.depgraph.dependency.DependencyNode;
import com.github.depgraph.dependency.AbstractDependencyNodeNameRendererTest;
import com.github.depgraph.graph.NodeRenderer;

public class Neo4jDependencyNodeNameRendererTest extends AbstractDependencyNodeNameRendererTest {

  @Override
  protected NodeRenderer<DependencyNode> createNodeNameRenderer(boolean showGroupId, boolean showArtifactId, boolean showTypes, boolean showClassifier, boolean showVersion, boolean showOptional) {
    return new Neo4jDependencyNodeNameRenderer(showGroupId, showArtifactId, showTypes, showClassifier, showVersion, showOptional);
  }

  @Override
  protected String renderNothingResult() {
    return "{\"scope\":\"compile\"}";
  }

  @Override
  protected String renderGroupIdResult() {
    return "{\"groupId\":\"groupId\",\"scope\":\"compile\"}";
  }

  @Override
  protected String renderArtifactIdResult() {
    return "{\"artifactId\":\"artifactId\",\"scope\":\"compile\"}";
  }

  @Override
  protected String renderVersionResult() {
    return "{\"version\":\"version\",\"scope\":\"compile\"}";
  }

  @Override
  protected String renderOptionalResult() {
    return "{\"optional\":true,\"scope\":\"compile\"}";
  }

  @Override
  protected String renderGroupIdArtifactIdVersionResult() {
    return "{\"groupId\":\"groupId\",\"artifactId\":\"artifactId\",\"version\":\"version\",\"scope\":\"compile\"}";
  }

  @Override
  protected String renderGroupIdArtifactIdVersionOptionalResult() {
    return "{\"groupId\":\"groupId\",\"artifactId\":\"artifactId\",\"version\":\"version\",\"optional\":true,\"scope\":\"compile\"}";
  }

  @Override
  protected String renderGroupIdArtifactIdResult() {
    return "{\"groupId\":\"groupId\",\"artifactId\":\"artifactId\",\"scope\":\"compile\"}";
  }

  @Override
  protected String renderGroupIdArtifactIdOptionalResult() {
    return "{\"groupId\":\"groupId\",\"artifactId\":\"artifactId\",\"optional\":true,\"scope\":\"compile\"}";
  }

  @Override
  protected String renderGroupIdVersionResult() {
    return "{\"groupId\":\"groupId\",\"version\":\"version\",\"scope\":\"compile\"}";
  }

  @Override
  protected String renderGroupIdVersionOptionalResult() {
    return "{\"groupId\":\"groupId\",\"version\":\"version\",\"optional\":true,\"scope\":\"compile\"}";
  }

  @Override
  protected String renderArtifactIdVersionResult() {
    return "{\"artifactId\":\"artifactId\",\"version\":\"version\",\"scope\":\"compile\"}";
  }

  @Override
  protected String renderArtifactIdVersionOptionalResult() {
    return "{\"artifactId\":\"artifactId\",\"version\":\"version\",\"optional\":true,\"scope\":\"compile\"}";
  }


  @Override
  protected String renderTypesResult() {
    return "{\"artifactId\":\"artifactId\",\"scope\":\"compile\",\"type\":\"jar\"}";
  }

  @Override
  protected String renderJarTypeOnlyResult() {
    return "{\"artifactId\":\"artifactId\",\"scope\":\"compile\",\"type\":\"jar\"}";
  }

  @Override
  protected String renderClassifiersResult() {
    return "{\"artifactId\":\"artifactId\",\"scope\":\"compile\"}";
  }

  @Override
  protected String renderEmptyClassifierResult() {
    return "{\"artifactId\":\"artifactId\",\"scope\":\"compile\"}";
  }

  @Override
  protected String renderAllResult() {
    return "{\"groupId\":\"groupId\",\"artifactId\":\"artifactId\",\"version\":\"version\",\"scope\":\"test\",\"type\":\"jar\"}";
  }
}
