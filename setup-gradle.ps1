# Setup Gradle Wrapper Script
# This script downloads and configures the Gradle wrapper

$ErrorActionPreference = "Stop"

Write-Host "=== Setting up Gradle Wrapper ===" -ForegroundColor Cyan

# Create gradle/wrapper directory if it doesn't exist
$gradleWrapperDir = "gradle\wrapper"
if (-not (Test-Path $gradleWrapperDir)) {
    New-Item -ItemType Directory -Path $gradleWrapperDir -Force | Out-Null
}

# Download gradle-wrapper.jar
$gradleVersion = "8.11.1"
$wrapperJarUrl = "https://services.gradle.org/distributions/gradle-${gradleVersion}-wrapper.jar"
$wrapperJarPath = "${gradleWrapperDir}\gradle-wrapper.jar"

Write-Host "Downloading Gradle Wrapper JAR..."
try {
    # Use BITS to download the file
    Start-BitsTransfer -Source $wrapperJarUrl -Destination $wrapperJarPath
    Write-Host "Downloaded Gradle Wrapper JAR to ${wrapperJarPath}" -ForegroundColor Green
} catch {
    Write-Host "Failed to download Gradle Wrapper JAR: $_" -ForegroundColor Red
    exit 1
}

# Create gradlew.bat if it doesn't exist
if (-not (Test-Path "gradlew.bat")) {
    Write-Host "Creating gradlew.bat..."
    @"
@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME
set APP_HOME=%APP_HOME:\\=/%
set APP_HOME=%APP_HOME:/=\%

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >nul 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% ^
  -Dorg.gradle.appname="%APP_BASE_NAME" ^
  -classpath "%CLASSPATH%" ^
  org.gradle.wrapper.GradleWrapperMain %*

:end
@rem End local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" endlocal

:omega
"@ | Out-File -FilePath "gradlew.bat" -Encoding ASCII
    Write-Host "Created gradlew.bat" -ForegroundColor Green
}

Write-Host "=== Gradle Wrapper setup complete ===" -ForegroundColor Green
Write-Host "You can now run: .\gradlew.bat build" -ForegroundColor Cyan
