@echo off
setlocal

:: Set Java home to JDK 17
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.15+6
set PATH=%JAVA_HOME%\bin;%PATH%

:: Set Android home
set ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk
set PATH=%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin;%PATH%

echo Java version:
"%JAVA_HOME%\bin\java.exe" -version
echo.
echo ADB version:
adb version
echo.

:: Clean and build the project
echo Building the project...
call gradlew clean assembleDebug --stacktrace -Dorg.gradle.java.home="%JAVA_HOME%"

if %ERRORLEVEL% EQU 0 (
    echo Build successful. Installing the app...
    adb install -r app\build\outputs\apk\debug\app-debug.apk
    
    if %ERRORLEVEL% EQU 0 (
        echo Launching the app...
        adb shell am start -n dev.aurakai.auraframefx/.MainActivity
    ) else (
        echo Failed to install the app.
    )
) else (
    echo Build failed. Please check the error messages above.
)

endlocal
