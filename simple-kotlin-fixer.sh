#!/bin/bash

# Simple Kotlin Visibility Fixer Script
# This script uses grep and sed to find and add public modifiers to Kotlin files

set -e

echo "=== Simple Kotlin Visibility Fixer ==="

# Process all .kt files in the app directory
find ./app -name "*.kt" -type f | while read -r file; do
    echo "Processing $file"
    
    # Add 'public' to class, interface, object declarations if not already there
    sed -i 's/^\(\s*\)\(class\|interface\|object\|data class\|enum class\|sealed class\)\(\s\+\)/\1public \2\3/g' "$file"
    
    # Add 'public' to function declarations if not already there
    sed -i 's/^\(\s*\)\(fun\)\(\s\+\)/\1public \2\3/g' "$file"
    
    # Add 'public' to property declarations at top level if not already there
    sed -i 's/^\(\s*\)\(val\|var\)\(\s\+\)/\1public \2\3/g' "$file"
    
    # Add 'public' to const val declarations if not already there
    sed -i 's/^\(\s*\)\(const val\)\(\s\+\)/\1public \2\3/g' "$file"
done

# Fix specific Color issues in Color.kt
COLOR_FILES=$(find ./app -name "Color.kt" -o -name "Colors.kt")
for file in $COLOR_FILES; do
    echo "Fixing color definitions in $file"
    
    # Rename conflicting color names
    sed -i 's/NeonTeal/NeonTealColor/g' "$file"
    sed -i 's/NeonPurple/NeonPurpleColor/g' "$file"
    sed -i 's/NeonCyan/NeonCyanColor/g' "$file"
    sed -i 's/NeonGreen/NeonGreenColor/g' "$file"
    sed -i 's/NeonYellow/NeonYellowColor/g' "$file"
    sed -i 's/NeonRed/NeonRedColor/g' "$file"
    
    # Add Color return type
    sed -i 's/\(public val [A-Za-z0-9_]*Color\) =/\1: Color =/g' "$file"
done

# Fix HaloView.kt to use the renamed colors
HALO_VIEW_FILE=$(find ./app -name "HaloView.kt")
if [ -n "$HALO_VIEW_FILE" ]; then
    echo "Fixing color references in HaloView.kt"
    sed -i 's/NeonTeal/NeonTealColor/g' "$HALO_VIEW_FILE"
    sed -i 's/NeonPurple/NeonPurpleColor/g' "$HALO_VIEW_FILE"
fi

# Fix NavDestination.kt return types
NAV_DEST_FILE=$(find ./app -name "NavDestination.kt")
if [ -n "$NAV_DEST_FILE" ]; then
    echo "Fixing NavDestination.kt return types"
    sed -i 's/\(public fun getRoute\)()/\1(): String/g' "$NAV_DEST_FILE"
    sed -i 's/\(public fun getTitle\)()/\1(): String/g' "$NAV_DEST_FILE"
fi

echo "Done!"
