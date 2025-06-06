#!/bin/bash
# Fix gradlew permissions and push to GitHub

# Make gradlew executable
chmod +x gradlew

# Check if the file has executable permissions
ls -la gradlew

# Update git to track the file with executable permissions
git update-index --chmod=+x gradlew

# Add the file to git
git add gradlew

# Commit the change
git commit -m "Make gradlew executable"

# Push to GitHub
git push origin gradle-fixes

echo "Done!"