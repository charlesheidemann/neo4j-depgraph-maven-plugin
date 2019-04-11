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
package com.github.depgraph.dependency;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;

/**
 * Representation of a dependency graph node. It adapts these Maven-specific classes:
 * <ul>
 * <li>{@link org.apache.maven.artifact.Artifact}</li>
 * <li>{@link org.eclipse.aether.graph.DependencyNode}</li>
 * </ul>
 */
public final class DependencyNode {

  private final Artifact artifact;
  private final String effectiveVersion;
  private final NodeResolution resolution;
  private final String scope;
  private final String classifier;
  private final String type;


  public DependencyNode(Artifact artifact) {
    this(artifact, determineNodeResolution(artifact), artifact.getVersion());
  }

  public DependencyNode(org.eclipse.aether.graph.DependencyNode dependencyNode) {
    this(createMavenArtifact(dependencyNode), determineResolution(dependencyNode),
        determineEffectiveVersion(dependencyNode));
  }

  private DependencyNode(Artifact artifact, NodeResolution resolution, String effectiveVersion) {
    if (artifact == null) {
      throw new NullPointerException("Artifact must not be null");
    }

    // FIXME: better create a copy of the artifact and set the missing attributes there.
    if (artifact.getScope() == null) {
      artifact.setScope("compile");
    }

    this.effectiveVersion = effectiveVersion;
    this.scope = artifact.getScope();
    this.type = artifact.getType();
    this.artifact = artifact;
    this.resolution = resolution;
    this.classifier = artifact.getClassifier();

  }

  private static Artifact createMavenArtifact(org.eclipse.aether.graph.DependencyNode dependencyNode) {
    org.eclipse.aether.artifact.Artifact artifact = dependencyNode.getArtifact();
    String scope = null;
    boolean optional = false;
    if (dependencyNode.getDependency() != null) {
      scope = dependencyNode.getDependency().getScope();
      optional = dependencyNode.getDependency().isOptional();
    }

    DefaultArtifact mavenArtifact = new DefaultArtifact(
        artifact.getGroupId(),
        artifact.getArtifactId(),
        artifact.getVersion(),
        scope,
        artifact.getProperty("type", artifact.getExtension()),
        artifact.getClassifier(),
        null
    );
    mavenArtifact.setOptional(optional);

    return mavenArtifact;
  }

  private static NodeResolution determineResolution(org.eclipse.aether.graph.DependencyNode dependencyNode) {
    org.eclipse.aether.graph.DependencyNode winner = (org.eclipse.aether.graph.DependencyNode) dependencyNode.getData()
        .get(ConflictResolver.NODE_DATA_WINNER);

    if (winner != null) {
      if (winner.getArtifact().getVersion().equals(dependencyNode.getArtifact().getVersion())) {
        return NodeResolution.OMITTED_FOR_DUPLICATE;
      }

      return NodeResolution.OMITTED_FOR_CONFLICT;
    }

    return NodeResolution.INCLUDED;
  }

  private static NodeResolution determineNodeResolution(Artifact artifact) {
    if (artifact.getScope() == null) {
      return NodeResolution.PARENT;
    }

    return NodeResolution.INCLUDED;
  }

  private static String determineEffectiveVersion(org.eclipse.aether.graph.DependencyNode dependencyNode) {
    org.eclipse.aether.graph.DependencyNode winner = (org.eclipse.aether.graph.DependencyNode) dependencyNode.getData()
        .get(ConflictResolver.NODE_DATA_WINNER);
    if (winner != null) {
      return winner.getArtifact().getVersion();
    }

    return dependencyNode.getArtifact().getVersion();
  }

  public void merge(DependencyNode other) {
    if (this == other) {
      return;
    }

    if (this.artifact.isOptional()) {
      this.artifact.setOptional(other.getArtifact().isOptional());
    }
  }

  public Artifact getArtifact() {
    return this.artifact;
  }

  public NodeResolution getResolution() {
    return this.resolution;
  }

  public String getScope() {
    return this.scope;
  }

  public String getClassifier() {
    return this.classifier;
  }

  public String getType() {
    return this.type;
  }

  /**
   * Returns the <strong>effective</strong> version of this node, i.e. the version that is actually used. This is
   * important for nodes with a resolution of {@link NodeResolution#OMITTED_FOR_CONFLICT} where {@code
   * getArtifact().getVersion()} will return the omitted version.
   *
   * @return The effective version of this node.
   */
  public String getEffectiveVersion() {
    return this.effectiveVersion;
  }

  /**
   * Returns the <strong>effective</strong> scope of this node, i.e. the scope that is actually used. This is important
   * if scopes are merged and a node may have more than one scope.
   *
   * @return The effective scope of this node.
   */
  public String getEffectiveScope() {
    return this.scope;
  }

  @Override
  public String toString() {
    return this.artifact.toString();
  }
}
