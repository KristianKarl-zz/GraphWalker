@echo off

rem   The script takes a folder containing graph files and a destination graphml
rem   file. The destination file will be the result of all files contained in the
rem   folder when merged. Please do not put the destination file in the same folder
rem   as the source folder.
rem
rem   This is useful when working with multiple graph files. In order to see the
rem   'complete picture', this utility merges all files into one file.

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

%JAVA% %JAVA_OPTIONS% org.tigris.mbt.ParseGraphs %1 %2
