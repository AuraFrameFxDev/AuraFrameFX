# Set environment variables
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:PATH = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\tools\bin;$env:ANDROID_HOME\tools;$env:ANDROID_HOME\emulator;$env:PATH"

# Verify Java and ADB
Write-Host "Java version:"
& "$env:JAVA_HOME\bin\java.exe" -version
Write-Host "`nADB version:"
adb version

# Clean and build the project
Write-Host "`nBuilding the project..."
.\gradlew clean assembleDebug --info

# Check if build was successful
if ($LASTEXITCODE -eq 0) {
    # Install the app
    Write-Host "`nInstalling the app..."
    adb install -r app\build\outputs\apk\debug\app-debug.apk
    
    # Launch the app
    Write-Host "`nLaunching the app..."
    adb shell am start -n dev.aurakai.auraframefx/.MainActivity
} else {
    Write-Host "Build failed. Please check the error messages above." -ForegroundColor Red
}
