#!/bin/bash

echo "Starting comprehensive Kotlin code fixer for AuraFrameFx..."

# Fix NeonTeal/NeonTealColor and NeonPurple/NeonPurpleColor in HaloView.kt
echo "Fixing color references in HaloView.kt..."
sed -i 's/NeonTeal\b/NeonTealColor/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/components/HaloView.kt
sed -i 's/NeonPurple\b/NeonPurpleColor/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/components/HaloView.kt

# Add import statements for NeonTealColor and NeonPurpleColor
echo "Adding missing imports for color references..."
sed -i '/import dev.aurakai.auraframefx.ui.theme.NeonBlue/a import dev.aurakai.auraframefx.ui.theme.NeonTealColor\nimport dev.aurakai.auraframefx.ui.theme.NeonPurpleColor' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/components/HaloView.kt

# Fix other color imports if any are missing
sed -i 's/\/\/ val NeonTealColor = Color(0xFF00FFCC) \/\/ Brighter teal for accents/\/\/ Color definitions are now imported from Color.kt/' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/components/HaloView.kt
sed -i 's/\/\/ val NeonPurpleColor = Color(0xFFE000FF) \/\/ Slightly softer purple for readability/\/\/ Color definitions are now imported from Color.kt/' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/components/HaloView.kt

# Resolve duplicate color definitions in Color.kt
echo "Fixing duplicate color definitions in Color.kt..."
sed -i '10d' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt  # Remove duplicate NeonGreenColor
sed -i '12d' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt  # Remove duplicate NeonRedColor

# Ensure all colors in Color.kt have public modifiers and explicit return types
echo "Ensuring all colors in Color.kt have public modifiers and explicit return types..."
sed -i 's/public val NeonBlue = Color/public val NeonBlue: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val NeonPink = Color/public val NeonPink: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val DarkBackground = Color/public val DarkBackground: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val Surface = Color/public val Surface: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val SurfaceVariant = Color/public val SurfaceVariant: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val CardBackground = Color/public val CardBackground: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val OnSurface = Color/public val OnSurface: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val OnSurfaceVariant = Color/public val OnSurfaceVariant: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val OnPrimary = Color/public val OnPrimary: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val OnSecondary = Color/public val OnSecondary: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val Accent1 = Color/public val Accent1: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val Accent2 = Color/public val Accent2: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val Accent3 = Color/public val Accent3: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val Accent4 = Color/public val Accent4: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightPrimary = Color/public val LightPrimary: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightOnPrimary = Color/public val LightOnPrimary: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightSecondary = Color/public val LightSecondary: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightOnSecondary = Color/public val LightOnSecondary: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightTertiary = Color/public val LightTertiary: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightOnTertiary = Color/public val LightOnTertiary: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightBackground = Color/public val LightBackground: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightOnBackground = Color/public val LightOnBackground: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightSurface = Color/public val LightSurface: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightOnSurface = Color/public val LightOnSurface: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightError = Color/public val LightError: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val LightOnError = Color/public val LightOnError: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val GlowOverlay = Color/public val GlowOverlay: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val PulseOverlay = Color/public val PulseOverlay: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val HoverOverlay = Color/public val HoverOverlay: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt
sed -i 's/public val PressOverlay = Color/public val PressOverlay: Color = Color/g' /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt

# Clean up Colors.kt by removing duplicate color definitions that exist in Color.kt
echo "Cleaning up Colors.kt to avoid duplicates..."
cat > /workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Colors.kt << 'EOF'
package dev.aurakai.auraframefx.ui.theme

import androidx.compose.ui.graphics.Color

// This file is deprecated. All color definitions have been moved to Color.kt
// Please use imports from Color.kt instead
EOF

# Find all Kotlin files recursively and add public modifiers where missing
echo "Adding public modifiers to all Kotlin files for explicit API mode..."
find /workspaces/AuraFrameFx-Beta/app/src/main/java -name "*.kt" -type f | while read -r file; do
    # Fix properties without explicit visibility
    sed -i 's/^\(\s*\)\(val\|var\)\(\s\+\)/\1public \2\3/g' "$file"
    
    # Fix functions without explicit visibility
    sed -i 's/^\(\s*\)\(fun\)\(\s\+\)/\1public \2\3/g' "$file"
    
    # Fix classes/interfaces/objects without explicit visibility
    sed -i 's/^\(\s*\)\(class\|interface\|object\|enum class\|sealed class\|data class\)\(\s\+\)/\1public \2\3/g' "$file"
    
    # Fix companion objects without explicit visibility
    sed -i 's/^\(\s*\)\(companion object\)/\1public \2/g' "$file"
    
    # Fix constructors without explicit visibility
    sed -i 's/^\(\s*\)\(constructor\)\(\s*\)/\1public \2\3/g' "$file"
    
    echo "Added public modifiers to $file"
done

# Fix private properties that should remain private
echo "Preserving private/protected modifiers..."
find /workspaces/AuraFrameFx-Beta/app/src/main/java -name "*.kt" -type f -exec sed -i 's/public private/private/g' {} \;
find /workspaces/AuraFrameFx-Beta/app/src/main/java -name "*.kt" -type f -exec sed -i 's/public protected/protected/g' {} \;
find /workspaces/AuraFrameFx-Beta/app/src/main/java -name "*.kt" -type f -exec sed -i 's/public internal/internal/g' {} \;

echo "Fixing missing return types for properties and functions..."
find /workspaces/AuraFrameFx-Beta/app/src/main/java -name "*.kt" -type f | while read -r file; do
    # Add basic return type annotations for properties that don't have them
    # This is a simplistic approach - a complete solution would require parsing the Kotlin code
    sed -i 's/\(public val [a-zA-Z0-9_]\+\) = \([a-zA-Z0-9_]\+\)()/\1: \2 = \2()/g' "$file"
    
    echo "Fixed return types in $file"
done

echo "Comprehensive code fixing completed."
