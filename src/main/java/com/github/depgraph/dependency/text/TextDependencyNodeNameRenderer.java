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
package com.github.depgraph.dependency.text;

import com.github.depgraph.dependency.DependencyNode;
import com.github.depgraph.graph.NodeRenderer;
import com.google.common.base.Joiner;
import org.apache.maven.artifact.Artifact;

public class TextDependencyNodeNameRenderer implements NodeRenderer<DependencyNode> {

  private static final Joiner COLON_JOINER = Joiner.on(":").skipNulls();

  private final boolean showGroupId;
  private final boolean showArtifactId;
  private final boolean showTypes;
  private final boolean showClassifiers;
  private final boolean showVersion;
  private final boolean showOptional;

  public TextDependencyNodeNameRenderer(boolean showGroupId, boolean showArtifactId, boolean showTypes,
      boolean showClassifiers, boolean showVersionsOnNodes, boolean showOptional) {
    this.showGroupId = showGroupId;
    this.showArtifactId = showArtifactId;
    this.showTypes = showTypes;
    this.showClassifiers = showClassifiers;
    this.showVersion = showVersionsOnNodes;
    this.showOptional = showOptional;
  }

  @Override
  public String render(DependencyNode node) {
    Artifact artifact = node.getArtifact();

    String artifactString = COLON_JOINER.join(
        this.showGroupId ? artifact.getGroupId() : null,
        this.showArtifactId ? artifact.getArtifactId() : null,
        this.showVersion ? node.getEffectiveVersion() : null,
        this.showTypes ? node.getType() : null,
        this.showClassifiers ? node.getClassifier() : null,
        node.getScope());

    if (this.showOptional && artifact.isOptional()) {
      return artifactString + " (optional)";
    }

    return artifactString;
  }

}
