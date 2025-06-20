#!/bin/bash

echo "Starting AuraFrameFx-Beta Kotlin Fix Script"

# Create a timestamp for logging
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="/workspaces/AuraFrameFx-Beta/fix_script_$TIMESTAMP.log"

# Function to log messages
log() {
  echo "[$(date +"%Y-%m-%d %H:%M:%S")] $1" | tee -a "$LOG_FILE"
}

# 1. Fix duplicates in Color.kt
log "Fixing duplicate color definitions in Color.kt"
COLOR_FILE="/workspaces/AuraFrameFx-Beta/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt"

# Create a backup
cp "$COLOR_FILE" "${COLOR_FILE}.backup"

# Fix duplicate NeonRedColor declaration - removing the second one at line 10
awk '!seen[$0]++' "$COLOR_FILE" > "${COLOR_FILE}.tmp" && mv "${COLOR_FILE}.tmp" "$COLOR_FILE"

# 2. Add explicit API mode configuration to build.gradle.kts
log "Adding explicit API mode configuration to build.gradle.kts"

# Find the kotlin block in build.gradle.kts and add explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict
BUILD_GRADLE="/workspaces/AuraFrameFx-Beta/app/build.gradle.kts"
cp "$BUILD_GRADLE" "${BUILD_GRADLE}.backup"

# Use sed to add the explicitApi line
sed -i '/kotlin {/a\\    explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict' "$BUILD_GRADLE"

# 3. Fix return types in all Kotlin files
log "Fixing return types in all Kotlin files"

find /workspaces/AuraFrameFx-Beta/app/src/main/java -name "*.kt" | while read -r file; do
  log "Processing file: $file"
  
  # Backup the file
  cp "$file" "${file}.backup"
  
  # Add public modifier to declarations without visibility modifiers
  sed -i 's/^\(\s*\)\(val\|var\)\(\s\+\)/\1public \2\3/g' "$file"
  sed -i 's/^\(\s*\)\(fun\)\(\s\+\)/\1public \2\3/g' "$file"
  sed -i 's/^\(\s*\)\(class\|interface\|object\|enum class\|sealed class\|data class\)\(\s\+\)/\1public \2\3/g' "$file"
  
  # Fix return types for functions
  # This is a simplified approach - in a real scenario, you'd need a proper Kotlin parser
  
  # Fix functions that return Unit but don't specify it
  sed -i 's/\(public fun [a-zA-Z0-9_]\+\)(\([^)]*\))\s*{/\1(\2): Unit {/g' "$file"
  
  # Fix property return types
  # Add Color return type for color properties
  sed -i 's/\(public val [a-zA-Z0-9_]*Color\) = Color(/\1: Color = Color(/g' "$file"
  
  # Restore visibility modifiers that should not be public
  sed -i 's/public private/private/g' "$file"
  sed -i 's/public protected/protected/g' "$file"
  sed -i 's/public internal/internal/g' "$file"
  
  log "Completed processing: $file"
done

# 4. Run the existing visibility fixer script
log "Running the visibility fixer script"
chmod +x /workspaces/AuraFrameFx-Beta/fix-kotlin-visibility.sh
/workspaces/AuraFrameFx-Beta/fix-kotlin-visibility.sh

# 5. Create a task file marker for the fixVisibility task
log "Creating task marker file for Gradle caching"
mkdir -p /workspaces/AuraFrameFx-Beta/app/build/tmp
echo "Visibility fixing completed at $(date)" > /workspaces/AuraFrameFx-Beta/app/build/tmp/fixVisibility.marker

log "Script completed successfully"
echo "Full log available at: $LOG_FILE"
