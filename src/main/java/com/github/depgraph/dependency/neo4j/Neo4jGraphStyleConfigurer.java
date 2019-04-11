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

import com.github.depgraph.dependency.AbstractGraphStyleConfigurer;
import com.github.depgraph.dependency.DependencyNode;
import com.github.depgraph.graph.GraphBuilder;
import com.github.depgraph.graph.neo4j.Neo4jGraphFormatter;


public class Neo4jGraphStyleConfigurer extends AbstractGraphStyleConfigurer {

  @Override
  public GraphBuilder<DependencyNode> configure(GraphBuilder<DependencyNode> graphBuilder) {
    return graphBuilder
        .useNodeNameRenderer(new Neo4jDependencyNodeNameRenderer(this.showGroupId, this.showArtifactId, this.showTypes,
            this.showClassifiers, this.showVersionsOnNodes, this.showOptional))
        .useEdgeRenderer(new Neo4jDependencyEdgeRenderer(this.showVersionOnEdges))
        .graphFormatter(new Neo4jGraphFormatter(graphBuilder.getNeo4jUri(), graphBuilder.getNeo4AuthToken()));
  }
}
