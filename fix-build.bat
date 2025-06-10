@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
echo Setting JAVA_HOME to %JAVA_HOME%
.\gradlew clean tasks --info
pause