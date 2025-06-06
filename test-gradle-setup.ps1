# PowerShell script to test Gradle setup with settings similar to GitHub Actions
$ErrorActionPreference = "Stop"

Write-Host "Testing Gradle setup with settings similar to GitHub Actions..." -ForegroundColor Cyan

# Use Java 21 if available - modify this path to match your local Java 21 installation
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
Write-Host "Using JAVA_HOME: $env:JAVA_HOME"

# Check if Java is available
try {
    $javaVersion = & java -version 2>&1
    Write-Host "Java version:" -ForegroundColor Green
    Write-Host $javaVersion -ForegroundColor Green
}
catch {
    Write-Host "Error: Java not found or not properly set up. Please install Java 21." -ForegroundColor Red
    exit 1
}

# Make gradlew executable (Windows doesn't really need this but for completeness)
Write-Host "Making gradlew executable..." -ForegroundColor Cyan
& git update-index --chmod=+x gradlew

# Test basic Gradle help command
Write-Host "Testing ./gradlew help..." -ForegroundColor Cyan
try {
    & ./gradlew help
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Gradle project validated successfully with wrapper!" -ForegroundColor Green
    }
    else {
        Write-Host "✗ Gradle wrapper command failed with exit code $LASTEXITCODE" -ForegroundColor Red
    }
}
catch {
    Write-Host "✗ Error running Gradle wrapper command: $_" -ForegroundColor Red
    
    # Try with global gradle if wrapper fails
    Write-Host "Testing with global gradle if available..." -ForegroundColor Yellow
    try {
        & gradle help
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Gradle project validated successfully with global Gradle!" -ForegroundColor Green
        }
        else {
            Write-Host "✗ Global Gradle command failed with exit code $LASTEXITCODE" -ForegroundColor Red
        }
    }
    catch {
        Write-Host "✗ Error running global Gradle command: $_" -ForegroundColor Red
        Write-Host "! Gradle validation failed in repository root" -ForegroundColor Red
    }
}

Write-Host "Done!" -ForegroundColor Cyan
pause