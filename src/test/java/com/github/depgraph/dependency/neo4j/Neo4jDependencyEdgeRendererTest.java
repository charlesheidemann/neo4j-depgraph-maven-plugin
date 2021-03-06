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

import com.github.depgraph.dependency.AbstractDependencyEdgeRendererTest;
import com.github.depgraph.dependency.DependencyNode;
import com.github.depgraph.graph.EdgeRenderer;

public class Neo4jDependencyEdgeRendererTest extends AbstractDependencyEdgeRendererTest {

  @Override
  protected EdgeRenderer<DependencyNode> createEdgeRenderer(boolean renderVersion) {
    return new Neo4jDependencyEdgeRenderer(renderVersion);
  }

  @Override
  protected String renderWithoutVersionResult() {
    return "{\"resolution\":\"INCLUDED\",\"resolutionScope\":\"compile\"}";
  }

  @Override
  protected String renderWithNonConflictingVersionResult() {
    return "{\"resolution\":\"INCLUDED\",\"resolutionScope\":\"compile\"}";
  }

  @Override
  protected String renderWithConflictShowingVersionResult() {
    return "{\"resolution\":\"OMITTED_FOR_CONFLICT\",\"resolutionScope\":\"compile\"}";
  }

  @Override
  protected String renderWithConflictNotShowingVersionResult() {
    return "{\"resolution\":\"OMITTED_FOR_CONFLICT\",\"resolutionScope\":\"compile\"}";
  }

  @Override
  protected String renderWithDuplicateResult() {
    return "{\"resolution\":\"OMITTED_FOR_DUPLICATE\",\"resolutionScope\":\"compile\"}";
  }
}
