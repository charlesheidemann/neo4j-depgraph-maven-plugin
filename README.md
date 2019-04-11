# neo4j-depgraph-maven-plugin
*- A Maven plugin based on [depgraph-maven-plugin](https://ferstl.github.io/depgraph-maven-plugin/plugin-info.html) that store the dependency graph in Neo4j*

This Maven plugin generates dependency graphs on single modules or in an aggregated form on multi-module projects. 
The dependency graphs is stored in a Neo4J graph database. 

For more information take a look at the original [Depgraph Plugin Documentation](https://ferstl.github.io/depgraph-maven-plugin/plugin-info.html), the [Release Notes](https://github.com/ferstl/depgraph-maven-plugin/releases) and the [Wiki](https://github.com/ferstl/depgraph-maven-plugin/wiki).

### Run on the Command Line
you need to fully qualify the plugin on the command line, e.g.:
   
    mvn com.github.depgraph:neo4j-depgraph-maven-plugin:1.0.0-SNAPSHOT:neo4j