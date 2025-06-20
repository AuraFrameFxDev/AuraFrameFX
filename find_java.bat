@echo off
echo Searching for Java installations...
echo.

echo Checking common Java installation directories...

set "java_paths="

:: Check Program Files directories
if exist "C:\Program Files\Java\jdk-17\bin\java.exe" (
    set "java_paths=%java_paths%;C:\Program Files\Java\jdk-17"
    echo Found Java 17 at: C:\Program Files\Java\jdk-17
)

if exist "C:\Program Files\Java\jdk-17.0.10\bin\java.exe" (
    set "java_paths=%java_paths%;C:\Program Files\Java\jdk-17.0.10"
    echo Found Java 17.0.10 at: C:\Program Files\Java\jdk-17.0.10
)

:: Check Program Files (x86)
if exist "C:\Program Files (x86)\Java\jdk-17\bin\java.exe" (
    set "java_paths=%java_paths%;C:\Program Files (x86)\Java\jdk-17"
    echo Found Java 17 at: C:\Program Files (x86)\Java\jdk-17
)

:: Check for AdoptOpenJDK/Temurin
if exist "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-17.0.10.7-hotspot\bin\java.exe" (
    set "java_paths=%java_paths%;%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-17.0.10.7-hotspot"
    echo Found Eclipse Adoptium Java 17 at: %LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-17.0.10.7-hotspot
)

:: Check for Oracle JDK
if exist "C:\Program Files\Java\jdk-17.0.10\bin\java.exe" (
    set "java_paths=%java_paths%;C:\Program Files\Java\jdk-17.0.10"
    echo Found Oracle JDK 17.0.10 at: C:\Program Files\Java\jdk-17.0.10
)

echo.
if "%java_paths%"=="" (
    echo No Java 17 installations found.
    echo Please download and install Java 17 from: https://adoptium.net/temurin/releases/?version=17
) else (
    echo.
    echo To use one of these Java installations, set JAVA_HOME to one of the paths above.
    echo For example:
    echo   set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    echo   set "PATH=%%JAVA_HOME%%\bin;%%PATH%%"
)

pause
