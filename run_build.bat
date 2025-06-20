@echo off
echo Starting build... > build_output.txt
call gradlew.bat clean build --no-daemon --stacktrace --info >> build_output.txt 2>&1
echo Build completed with status %ERRORLEVEL% >> build_output.txt
type build_output.txt
