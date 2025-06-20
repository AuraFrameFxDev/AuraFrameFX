#!/bin/bash

# This script adds public visibility modifiers to all Kotlin data classes that don't already have them

# Find all Kotlin files with data classes
find /workspaces/AuraFrameFx-Beta -name "*.kt" -type f | xargs grep -l "data class" | while read -r file; do
  echo "Processing $file"
  
  # Add 'public' to data class declarations if missing
  sed -i 's/\(^[[:space:]]*\)\(data class\)/\1public \2/g' "$file"
done

# Find all Kotlin files with normal classes
find /workspaces/AuraFrameFx-Beta -name "*.kt" -type f | xargs grep -l "class " | while read -r file; do
  echo "Processing $file for regular classes"
  
  # Add 'public' to class declarations if missing (and not already having visibility)
  sed -i 's/\(^[[:space:]]*\)\(class [^p]\)/\1public \2/g' "$file"
done

# Find all Kotlin files with interfaces
find /workspaces/AuraFrameFx-Beta -name "*.kt" -type f | xargs grep -l "interface " | while read -r file; do
  echo "Processing $file for interfaces"
  
  # Add 'public' to interface declarations if missing
  sed -i 's/\(^[[:space:]]*\)\(interface\)/\1public \2/g' "$file"
done

# Find all Kotlin files with top-level functions
find /workspaces/AuraFrameFx-Beta -name "*.kt" -type f | xargs grep -l "fun " | while read -r file; do
  echo "Processing $file for functions"
  
  # Add 'public' to top-level function declarations if missing
  sed -i 's/\(^[[:space:]]*\)\(fun [a-zA-Z]\)/\1public \2/g' "$file"
done

# Find all Kotlin files with object declarations
find /workspaces/AuraFrameFx-Beta -name "*.kt" -type f | xargs grep -l "object " | while read -r file; do
  echo "Processing $file for objects"
  
  # Add 'public' to object declarations if missing
  sed -i 's/\(^[[:space:]]*\)\(object\)/\1public \2/g' "$file"
done

# Find all Kotlin files with enum class declarations
find /workspaces/AuraFrameFx-Beta -name "*.kt" -type f | xargs grep -l "enum class" | while read -r file; do
  echo "Processing $file for enum classes"
  
  # Add 'public' to enum class declarations if missing
  sed -i 's/\(^[[:space:]]*\)\(enum class\)/\1public \2/g' "$file"
done

echo "Completed processing all Kotlin files for visibility modifiers."
