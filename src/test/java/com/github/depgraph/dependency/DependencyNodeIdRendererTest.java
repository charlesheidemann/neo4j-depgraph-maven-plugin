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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.junit.jupiter.api.Test;

class DependencyNodeIdRendererTest {

  @Test
  void groupId() {
    // act
    String result = DependencyNodeIdRenderer.groupId().render(createDependencyNode());

    // assert
    assertEquals("groupId", result);
  }

  @Test
  void versionLessId() {
    // act
    String result = DependencyNodeIdRenderer.versionlessId().render(createDependencyNode());

    // assert
    assertEquals("groupId:artifactId:1.0.0", result);
  }

  @Test
  void versionLessIdWithType() {
    // act
    String result = DependencyNodeIdRenderer.versionlessId()
        .withType(true)
        .render(createDependencyNode());

    // assert
    assertEquals("groupId:artifactId:type:1.0.0", result);
  }

  @Test
  void versionLessIdWithClassifier() {
    // act
    String result = DependencyNodeIdRenderer.versionlessId()
        .withClassifier(true)
        .render(createDependencyNode());

    // assert
    assertEquals("groupId:artifactId:1.0.0:classifier", result);
  }

  @Test
  void versionLessIdFull() {
    // act
    String result = DependencyNodeIdRenderer.versionlessId()
        .withType(true)
        .withScope(true)
        .withClassifier(true)
        .render(createDependencyNode());

    // assert
    assertEquals("groupId:artifactId:type:1.0.0:classifier:compile", result);
  }

  @Test
  void versionLessIdWithScope() {
    // act
    String result = DependencyNodeIdRenderer.versionlessId().withScope(true).render(createDependencyNode());

    // assert
    assertEquals("groupId:artifactId:1.0.0:compile", result);
  }


  @Test
  void versionLessIdWithEmptyClassifier() {
    // arrange
    Artifact artifact = new DefaultArtifact(
        "groupId",
        "artifactId",
        "1.0.0",
        "compile",
        "type",
        "",
        null);

    // act
    String result = DependencyNodeIdRenderer.versionlessId().withType(true).render(new DependencyNode(artifact));

    // assert
    assertEquals("groupId:artifactId:type:1.0.0", result);
  }

  private static DependencyNode createDependencyNode() {
    Artifact artifact = new DefaultArtifact(
        "groupId",
        "artifactId",
        "1.0.0",
        "compile",
        "type",
        "classifier",
        null);

    return new DependencyNode(artifact);
  }
}
