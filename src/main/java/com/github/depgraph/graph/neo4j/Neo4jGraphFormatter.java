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
package com.github.depgraph.graph.neo4j;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.depgraph.graph.Edge;
import com.github.depgraph.graph.GraphFormatter;
import com.github.depgraph.graph.Node;
import com.github.depgraph.graph.neo4j.Neo4jGraph.Artifact;
import com.github.depgraph.graph.neo4j.Neo4jGraph.Dependency;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

public class Neo4jGraphFormatter implements GraphFormatter {

  private static final String CREATE_DEPENDENCY_STATEMENT = " MATCH (from:Artifact),(to:Artifact) "
      + " WHERE from.id = $from AND to.id = $to "
      + " MERGE (from)-[r:DEPENDENCY { scope:$resolutionScope, name: $to + ':' + $resolutionScope } ]->(to) "
      + " RETURN type(r), r.name";
  private final String neo4jUri;
  private final AuthToken neo4AuthToken;

  private final ObjectMapper objectMapper = new ObjectMapper()
      .setSerializationInclusion(NON_EMPTY)
      .setVisibility(FIELD, ANY);

  private static final String CREATE_ARTIFACT_STATEMENT = " MERGE (artifact:Artifact { id:$id }) "
      + " ON CREATE SET artifact.id = $id, artifact.groupId = $groupId, artifact.artifactId = $artifactId, artifact.version = $version, artifact.scope = $scope, artifact.type = $type "
      + " RETURN artifact";

  public Neo4jGraphFormatter(String neo4jUri, AuthToken neo4AuthToken) {
    this.neo4jUri = neo4jUri;
    this.neo4AuthToken = neo4AuthToken;
  }

  @Override
  public String format(String graphName, Collection<Node<?>> nodes, Collection<Edge> edges) {
    Map<String, Integer> nodeIdMap = new HashMap<>(nodes.size());
    Neo4jGraph jsonGraph = new Neo4jGraph(graphName);
    List<Artifact> artifacts = new LinkedList<>();
    List<Dependency> dependencies = new LinkedList<>();

    int numericNodeId = 0;
    for (Node<?> node : nodes) {
      String nodeId = node.getNodeId();
      nodeIdMap.put(nodeId, numericNodeId++);
      artifacts.add(jsonGraph.addArtifact(nodeId, numericNodeId, readJson(node.getNodeName())));
    }

    for (Edge edge : edges) {
      String fromNodeId = edge.getFromNodeId();
      Integer fromNodeIdNumeric = nodeIdMap.get(fromNodeId);
      String toNodeId = edge.getToNodeId();
      Integer toNodeIdNumeric = nodeIdMap.get(toNodeId);
      dependencies.add(jsonGraph.addDependency(fromNodeId, fromNodeIdNumeric, toNodeId, toNodeIdNumeric, readJson(edge.getName())));
    }

    saveGraphNeo4j(artifacts, dependencies);

    return serialize(jsonGraph);

  }

  public void saveGraphNeo4j(final List<Artifact> artifacts, final List<Dependency> dependencies) {
    try (
        Driver driver = GraphDatabase.driver(neo4jUri, neo4AuthToken);
        Session session = driver.session()
    ) {
      saveArtifacts.accept(artifacts, session);
      saveDependencies.accept(dependencies, session);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Map<?, ?> readJson(String json) {
    try {
      return this.objectMapper.readValue(json, Map.class);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read JSON '" + json + "'", e);
    }
  }

  private String serialize(Neo4jGraph neo4jGraph) {
    DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter()
        .withObjectIndenter(new DefaultIndenter("  ", "\n"));

    ObjectWriter writer = this.objectMapper.writer(prettyPrinter);
    StringWriter jsonWriter = new StringWriter();
    try {
      writer.writeValue(jsonWriter, neo4jGraph);
    } catch (IOException e) {
      // should never happen with StringWriter
      throw new IllegalStateException(e);
    }

    return jsonWriter.toString();
  }

  private BiConsumer<List<Artifact>, Session> saveArtifacts = (artifacts, session) -> artifacts.forEach(artifact -> {
    HashMap params = new HashMap(artifact.getData());
    params.put("id", artifact.getId());
    try {
      session.run(CREATE_ARTIFACT_STATEMENT, params);
    } catch (Exception e) {
      e.printStackTrace();
    }
  });

  private BiConsumer<List<Dependency>, Session> saveDependencies = (dependencies, session) -> dependencies.forEach(dependency -> {
    HashMap params = new HashMap<>(dependency.getData());
    params.put("from", dependency.getFrom());
    params.put("to", dependency.getTo());
    try {
      session.run(CREATE_DEPENDENCY_STATEMENT, params);
    } catch (Exception e) {
      e.printStackTrace();
    }
  });

}