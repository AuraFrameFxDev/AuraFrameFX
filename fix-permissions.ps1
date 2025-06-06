# PowerShell script to fix executable permissions for gradlew in Git
Write-Host "Setting executable permissions for gradlew in Git..." -ForegroundColor Cyan

# Set the executable bit on gradlew
git update-index --chmod=+x gradlew

# Add the file to staging
git add gradlew

# Commit the change
git commit -m "Make gradlew executable"

# Push to GitHub
git push origin gradle-fixes

Write-Host "Done!" -ForegroundColor Green