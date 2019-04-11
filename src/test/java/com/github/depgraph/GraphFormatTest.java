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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GraphFormatTest {

  @Test
  void forNameJson() {
    // arrange
    String name1 = "json";
    String name2 = "Json";
    String name3 = "JSON";

    // act
    GraphFormat result1 = GraphFormat.forName(name1);
    GraphFormat result2 = GraphFormat.forName(name2);
    GraphFormat result3 = GraphFormat.forName(name3);

    // assert
    assertSame(GraphFormat.JSON, result2);
    assertSame(GraphFormat.JSON, result1);
    assertSame(GraphFormat.JSON, result3);
  }

  @Test
  void forNameText() {
    // arrange
    String name1 = "text";
    String name2 = "Text";
    String name3 = "TEXT";

    // act
    GraphFormat result1 = GraphFormat.forName(name1);
    GraphFormat result2 = GraphFormat.forName(name2);
    GraphFormat result3 = GraphFormat.forName(name3);

    // assert
    assertSame(GraphFormat.TEXT, result2);
    assertSame(GraphFormat.TEXT, result1);
    assertSame(GraphFormat.TEXT, result3);
  }

  @Test
  void forNameWithUnknownFormat() {
    // act/assert
    assertThrows(IllegalArgumentException.class, () -> GraphFormat.forName("unknown_format"));
  }

  @Test
  void getFileExtension() {
    assertEquals(".json", GraphFormat.JSON.getFileExtension());
    assertEquals(".txt", GraphFormat.TEXT.getFileExtension());
  }
}
