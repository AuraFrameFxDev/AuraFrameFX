@echo off
REM Temporarily set JAVA_HOME for this build session
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
echo Using JAVA_HOME=%JAVA_HOME%

REM Run Gradle build
call .\gradlew assembleDebug --stacktrace --info --no-daemon

echo.
echo Build completed. Press any key to close...
pause > nul
