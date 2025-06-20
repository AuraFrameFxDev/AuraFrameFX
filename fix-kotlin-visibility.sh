#!/bin/bash

# AuraFrameFx-Beta Kotlin Visibility Fixer
# This script compiles and runs the VisibilityFixer Java tool

set -e

echo "=== AuraFrameFx Kotlin Visibility Fixer ==="
echo "Compiling VisibilityFixer.java..."

# Create directories if they don't exist
mkdir -p /workspaces/AuraFrameFx-Beta/tools/bin

# Compile the Java source
javac -d /workspaces/AuraFrameFx-Beta/tools/bin /workspaces/AuraFrameFx-Beta/VisibilityFixer.java

echo "Running visibility fixer on codebase..."

# Run the tool
java -cp /workspaces/AuraFrameFx-Beta/tools/bin dev.aurakai.tools.VisibilityFixer /workspaces/AuraFrameFx-Beta "$@"

echo "Done!"
