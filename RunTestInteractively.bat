@echo off

rem   This demonstrates the Interactive mode of mbt.tigris.org
rem   The script takes a folder containing graph files, or a single graph file
rem   and prints a test sequence one by one, waiting for the user to either
rem   type a '0' or '1' (followd by a carriage return) to standard input.
rem   '0' means 'Give me the next edge or vertex in the test sequence.'
rem   '1' means 'Go back to the previous vertex, and give me an edge from that vertex.'
rem   Anything else stops the execution.

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

%JAVA% %JAVA_OPTIONS% org.tigris.mbt.RunTestInteractively -o -g %1
