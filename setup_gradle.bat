@echo off
setlocal enabledelayedexpansion

echo ===== Gradle Wrapper Setup =====
echo.

:: Check if Java is installed
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH.
    echo Please install Java 17 and ensure it's in your PATH.
    exit /b 1
)

:: Check Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set "jver=%%g"
    set jver=!jver:"=!
    echo Found Java version: !jver!
    
    if "!jver:~0,2!" LSS "17" (
        echo WARNING: Java version is less than 17. Please install Java 17 or higher.
        echo The build may fail with this Java version.
        pause
    )
)

:: Check if gradle/wrapper directory exists
if not exist "gradle\wrapper" (
    echo Creating gradle\wrapper directory...
    mkdir "gradle\wrapper"
)

echo.
echo ===== Gradle Wrapper Setup Complete =====
echo.
echo Next steps:
echo 1. Download gradle-wrapper.jar manually from:
echo    https://services.gradle.org/distributions/gradle-8.11.1-wrapper.jar
echo.
echo 2. Save it to: %~dp0gradle\wrapper\gradle-wrapper.jar
echo.
echo 3. Then run: .\gradlew.bat build
echo.
pause
