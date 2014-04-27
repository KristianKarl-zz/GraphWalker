# GraphWalker

[![Build Status](https://travis-ci.org/nilols/GraphWalker.png)](https://travis-ci.org/nilols/GraphWalker)

GraphWalker is a Model-Based Testing tool. It parses models and generates test sequences.

## Table of Contents
1\. [Modules](#modules)
1\.1 [graphwalker-core](#graphwalker-core)
1\.2 [graphwalker-maven-plugin](#graphwalker-maven-plugin)
1\.3 [graphwalker-jenkins-plugin](#graphwalker-jenkins-plugin)
1\.4 [graphwalker-example](#graphwalker-example)
2\. [Example](#example)  
2\.1  [pom.xml](#example-pom-xml)  
2\.2  [Example.java](#example-example-java)  
3\. [License](#license)  

<a name="modules"></a>
## 1\. Modules
<a name="graphwalker-core"></a>
### 1\.1 graphwalker-core
Contains the GraphWalker implementation 

<a name="graphwalker-maven-plugin"></a>
### 1\.2 graphwalker-maven-plugin

<a name="graphwalker-jenkins-plugin"></a>
### 1\.3 graphwalker-jenkins-plugin

<a name="graphwalker-example"></a>
### 1\.4 graphwalker-example
Showcase the GraphWalker project

```sh
mvn graphwalker:test
```

<a name="example"></a>
## 2\. Example

<a name="example-pom-xml"></a>
### 2\.1 pom.xml
Defines the dependencies needed to implement the model and the plugin needed to execution the model. A model can use actions and guards of any JSR 223 engine, but we need to add the dependency for the script engine.

```xml
...
<dependencies>
  <dependency>
    <groupId>org.graphwalker</groupId>
    <artifactId>graphwalker-core</artifactId>
    <version>3.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>groovy</groupId>
    <artifactId>grovvy-all</artifactId>
    <version>1.1-rc-1</version>
  </dependency>
</dependencies>
...
<build>
  <plugins>
    <plugin>
      <groupId>org.graphwalker</groupId>
      <artifactId>graphwalker-maven-plugin</artifactId>
      <version>3.0-SNAPSHOT</version>
    </plugin>
  </plugins>
</build>
...
```  

<a name="example-example-java"></a>
### 2\.2 Example.java 
Implements all the vertices and edges defined by the model, in this case the Example.graphml. Through the annotation we can define a stop condition that differs from the default one, if we create a class that implements the StopCondition interface we can tell GraphWalker to use that implementation through the stopCondition parameter in the @GraphWalker annotation.  

```java
...
import my.package.MyStopCondition;
...
@GraphWalker(id = "MyExample", model = "models/Example.graphml", stopCondition = MyStopCondition.class)
public class Example {
...
```  

<a name="license"></a>
## 3\. License

http://graphwalker.org/license/



