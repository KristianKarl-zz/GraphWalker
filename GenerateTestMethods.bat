@echo off

rem   The script takes a folder containing graph files, or a single graph file
rem   and prints an unique and sorted list of test methods that exists in the
rem   model.
rem   This is useful for retrieving all methods in order to implement them.

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

%JAVA% %JAVA_OPTIONS% org.tigris.mbt.GenerateTestMethods -g %1
