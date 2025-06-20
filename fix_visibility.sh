#!/bin/bash

# This script fixes Kotlin files that lack explicit visibility modifiers
# by adding 'public' modifiers to classes, interfaces, functions, etc.

# Find all Kotlin files in the generated directories
find /workspaces/AuraFrameFx-Beta -path "*/generated*" -name "*.kt" | while read -r file; do
  echo "Processing $file"
  
  # Add 'public' to class/interface/object/data class declarations
  # This uses awk to match patterns and prepend 'public' if missing
  awk '
    /^(class|interface|object|data class|enum class|sealed class|fun)/ && !/^public/ {
      print "public " $0
      next
    }
    {
      print
    }
  ' "$file" > "${file}.tmp"
  
  # Replace the original file with the modified version
  mv "${file}.tmp" "$file"
done

# Find all Kotlin files in the OpenAPI directories (if they exist)
find /workspaces/AuraFrameFx-Beta -path "*/openapi/src/main/kotlin*" -name "*.kt" | while read -r file; do
  echo "Processing $file"
  
  # Add 'public' to class/interface/object/data class declarations
  awk '
    /^(class|interface|object|data class|enum class|sealed class|fun)/ && !/^public/ {
      print "public " $0
      next
    }
    {
      print
    }
  ' "$file" > "${file}.tmp"
  
  # Replace the original file with the modified version
  mv "${file}.tmp" "$file"
done

echo "Completed processing all Kotlin files."
