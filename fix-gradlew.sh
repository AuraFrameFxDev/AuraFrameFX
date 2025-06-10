#!/bin/bash
# Fix gradlew permissions and push to GitHub

# Make gradlew executable
chmod +x ./gradlew

# Add the file to git
git add ./gradlew

# Commit the change
git commit -m "Make gradlew executable"

# Push to your branch (assuming you're on gradle-fixes)
git push origin gradle-fixes