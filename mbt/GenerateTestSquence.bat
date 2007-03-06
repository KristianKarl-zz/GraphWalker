@echo off

rem   The script takes a folder containing graph files, or a single graph file
rem   and prints an optimized test sequence of the model. mbt.tigris.org  will
rem   traverse all edges and vertices as quickly as possible. As soon as all
rem   edges have been visited, the test sequence ends.
rem
rem   This is useful for generating  a test sequence, that will traverse the
rem   model with as few steps as possible.

set JAVA_HOME="<PUT YOUR JAVA INSTALLATION FOLDER HERE. C:\Program Files\Java\j2re1.4.2_13>"
set JAVA=%JAVA_HOME%\bin\java

set CLASSPATH=mbt.jar
set CLASSPATH=%CLASSPATH%;colt.jar
set CLASSPATH=%CLASSPATH%;commons-cli-1.0.jar
set CLASSPATH=%CLASSPATH%;commons-collections-3.1.jar
set CLASSPATH=%CLASSPATH%;crimson-1.1.3.jar
set CLASSPATH=%CLASSPATH%;jdom.jar
set CLASSPATH=%CLASSPATH%;jung-1.7.2.jar
set CLASSPATH=%CLASSPATH%;log4j-1.2.8.jar

SET JAVA_OPTIONS=

@echo on
%JAVA% %JAVA_OPTIONS% org.tigris.mbt.GenerateTests -o -g %1
