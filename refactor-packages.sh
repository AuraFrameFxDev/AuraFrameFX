#!/bin/bash

# Script to refactor package names from com.example.app to dev.aurakai.auraframefx
# This should be run from the root of the project directory

echo "Starting package refactoring..."

# Create destination directories if they don't exist
mkdir -p app/src/main/java/dev/aurakai/auraframefx/{ai,model,ui,util}
mkdir -p app/src/main/java/dev/aurakai/auraframefx/ui/{animation,components,debug,navigation,theme}

# Copy all Kotlin files from old structure to new structure while renaming package references
echo "Copying and updating files..."

find app/src/main/java/com/example/app -name "*.kt" | while read file; do
  # Get the relative path (after com/example/app)
  rel_path=$(echo "$file" | sed 's|app/src/main/java/com/example/app/||')
  
  # Create the target directory
  target_dir="app/src/main/java/dev/aurakai/auraframefx/$(dirname "$rel_path")"
  mkdir -p "$target_dir"
  
  # Create the target file with updated package declarations
  target_file="app/src/main/java/dev/aurakai/auraframefx/$rel_path"
  
  # Replace package declarations and imports
  sed 's/package com\.example\.app/package dev.aurakai.auraframefx/g' "$file" | \
  sed 's/import com\.example\.app/import dev.aurakai.auraframefx/g' > "$target_file"
  
  echo "Processed: $file -> $target_file"
done

echo "Refactoring complete!"
echo "Note: Original files have not been removed. After verifying the changes, remove the old directory structure."
