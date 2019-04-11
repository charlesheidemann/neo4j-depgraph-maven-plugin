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

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.depgraph.dependency.DependencyNode;
import com.github.depgraph.dependency.NodeResolution;
import com.github.depgraph.graph.EdgeRenderer;
import java.io.IOException;
import java.io.StringWriter;

public class Neo4jDependencyEdgeRenderer implements EdgeRenderer<DependencyNode> {

  private final boolean renderVersions;
  private final ObjectMapper objectMapper;

  public Neo4jDependencyEdgeRenderer(boolean renderVersions) {
    this.renderVersions = renderVersions;
    this.objectMapper = new ObjectMapper()
        .setSerializationInclusion(NON_EMPTY)
        .setVisibility(FIELD, ANY);
  }

  @Override
  public String render(DependencyNode from, DependencyNode to) {
    NodeResolution resolution = to.getResolution();

    DependencyData dependencyData = new DependencyData(to.getEffectiveScope(), resolution);

    StringWriter jsonStringWriter = new StringWriter();
    try {
      this.objectMapper.writer().writeValue(jsonStringWriter, dependencyData);
    } catch (IOException e) {
      // should never happen with StringWriter
      throw new IllegalStateException(e);
    }

    return jsonStringWriter.toString();
  }

  private static class DependencyData {

    private final NodeResolution resolution;
    private final String resolutionScope;

    DependencyData(String resolutionScope, NodeResolution resolution) {
      this.resolution = resolution;
      this.resolutionScope = resolutionScope;
    }
  }
}
