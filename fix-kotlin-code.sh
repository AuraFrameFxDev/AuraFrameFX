#!/bin/bash

# Fix Kotlin Visibility and Common Issues Script
# This script automates fixing common issues in the codebase

set -e

echo "=== AuraFrameFx Code Fixer ==="

# 1. First run the visibility fixer
echo "Step 1: Running Kotlin Visibility Fixer"
chmod +x fix-kotlin-visibility.sh
./fix-kotlin-visibility.sh

# 2. Fix Color.kt and Colors.kt conflicts
echo "Step 2: Fixing Color.kt conflicts"

# Find the Color.kt files
COLOR_FILES=$(find ./app -name "Color.kt" -o -name "Colors.kt")

for file in $COLOR_FILES; do
    echo "Processing $file"
    
    # Add 'public' to all val declarations
    sed -i 's/\(val [A-Za-z0-9_]*\)/public \1/g' "$file"
    
    # Add Color return type to all color declarations
    sed -i 's/\(public val [A-Za-z0-9_]*\) =/\1: Color =/g' "$file"
    
    # Fix conflicting declarations by making them unique or qualifying them
    sed -i 's/public val NeonTeal: Color/public val NeonTealColor: Color/g' "$file"
    sed -i 's/public val NeonPurple: Color/public val NeonPurpleColor: Color/g' "$file"
    sed -i 's/public val NeonCyan: Color/public val NeonCyanColor: Color/g' "$file"
    sed -i 's/public val NeonGreen: Color/public val NeonGreenColor: Color/g' "$file"
    sed -i 's/public val NeonYellow: Color/public val NeonYellowColor: Color/g' "$file"
    sed -i 's/public val NeonRed: Color/public val NeonRedColor: Color/g' "$file"
done

# 3. Fix Dimensions.kt
echo "Step 3: Fixing Dimensions.kt"
DIMENSIONS_FILES=$(find ./app -name "Dimensions.kt")

for file in $DIMENSIONS_FILES; do
    echo "Processing $file"
    
    # Add 'public' to all val declarations
    sed -i 's/\(val [A-Za-z0-9_]*\)/public \1/g' "$file"
    
    # Add Dp return type to all dimension declarations
    sed -i 's/\(public val [A-Za-z0-9_]*\) =/\1: Dp =/g' "$file"
done

# 4. Fix Strings.kt
echo "Step 4: Fixing Strings.kt"
STRINGS_FILES=$(find ./app -name "Strings.kt")

for file in $STRINGS_FILES; do
    echo "Processing $file"
    
    # Add 'public' to all val declarations
    sed -i 's/\(val [A-Za-z0-9_]*\)/public \1/g' "$file"
    
    # Add String return type to all string declarations
    sed -i 's/\(public val [A-Za-z0-9_]*\) =/\1: String =/g' "$file"
done

# 5. Fix Shapes.kt
echo "Step 5: Fixing Shapes.kt"
SHAPES_FILES=$(find ./app -name "Shapes.kt")

for file in $SHAPES_FILES; do
    echo "Processing $file"
    
    # Add 'public' to all val declarations
    sed -i 's/\(val [A-Za-z0-9_]*\)/public \1/g' "$file"
    
    # Add appropriate return types
    sed -i 's/\(public val [A-Za-z0-9_]*Shape\) =/\1: Shape =/g' "$file"
    sed -i 's/\(public val [A-Za-z0-9_]*Corner\) =/\1: CornerSize =/g' "$file"
    sed -i 's/\(public val [A-Za-z0-9_]*Radius\) =/\1: Dp =/g' "$file"
done

# 6. Fix AppConstants.kt
echo "Step 6: Fixing AppConstants.kt"
CONSTANTS_FILES=$(find ./app -name "AppConstants.kt")

for file in $CONSTANTS_FILES; do
    echo "Processing $file"
    
    # Add 'public' to all val and const declarations
    sed -i 's/\(val [A-Za-z0-9_]*\)/public \1/g' "$file"
    sed -i 's/\(const val [A-Za-z0-9_]*\)/public \1/g' "$file"
    
    # Add appropriate return types
    sed -i 's/\(public val [A-Za-z0-9_]*PATH\) =/\1: String =/g' "$file"
    sed -i 's/\(public val [A-Za-z0-9_]*_URL\) =/\1: String =/g' "$file"
    sed -i 's/\(public const val [A-Za-z0-9_]*_CODE\) =/\1: Int =/g' "$file"
    sed -i 's/\(public const val [A-Za-z0-9_]*_TIMEOUT\) =/\1: Long =/g' "$file"
done

# 7. Fix HaloView.kt for NeonTeal and NeonPurple references
echo "Step 7: Fixing HaloView.kt color references"
HALO_VIEW_FILE=$(find ./app -name "HaloView.kt")

if [ -n "$HALO_VIEW_FILE" ]; then
    echo "Processing $HALO_VIEW_FILE"
    
    # Replace NeonTeal with NeonTealColor
    sed -i 's/NeonTeal/NeonTealColor/g' "$HALO_VIEW_FILE"
    
    # Replace NeonPurple with NeonPurpleColor
    sed -i 's/NeonPurple/NeonPurpleColor/g' "$HALO_VIEW_FILE"
fi

# 8. Fix ViewModels
echo "Step 8: Fixing ViewModels"
VIEWMODEL_FILES=$(find ./app -name "*ViewModel.kt")

for file in $VIEWMODEL_FILES; do
    echo "Processing $file"
    
    # Fix imports
    sed -i '1s/^/import androidx.lifecycle.ViewModel\n/' "$file"
    
    # Add 'public' to class declarations
    sed -i 's/\(class [A-Za-z0-9_]*ViewModel\)/public \1/g' "$file"
    
    # Add 'public' to function declarations
    sed -i 's/\(fun [A-Za-z0-9_]*\)/public \1/g' "$file"
done

# 9. Fix NavDestination.kt
echo "Step 9: Fixing NavDestination.kt"
NAV_DEST_FILE=$(find ./app -name "NavDestination.kt")

if [ -n "$NAV_DEST_FILE" ]; then
    echo "Processing $NAV_DEST_FILE"
    
    # Add 'public' to all declarations
    sed -i 's/\(interface [A-Za-z0-9_]*\)/public \1/g' "$file"
    sed -i 's/\(fun [A-Za-z0-9_]*\)/public \1/g' "$file"
    
    # Add return types to functions
    sed -i 's/\(public fun getRoute\)()/\1(): String/g' "$file"
    sed -i 's/\(public fun getTitle\)()/\1(): String/g' "$file"
done

# 10. Run build to see remaining errors
echo "Step 10: Running build to see remaining errors"
./gradlew app:compileDebugKotlin --stacktrace

echo "Done!"
