# Kotlin Visibility Fixer Tools

This directory contains tools to automatically fix Kotlin visibility issues in explicit API mode.

## Available Tools

### 1. Shell Script: `fix-kotlin-visibility.sh`

A simple bash script that uses Java to fix visibility issues in Kotlin files.

Usage:
```bash
./fix-kotlin-visibility.sh [directory] [--dry-run]
```

### 2. Gradle Task: `fixKotlinVisibility`

A Gradle task added to the root project that runs the visibility fixer on the entire codebase.

Usage:
```bash
./gradlew fixKotlinVisibility
```

### 3. Pre-build Integration

The visibility fixer is automatically integrated into the build process for the app module.
It runs before `preBuild` to ensure all Kotlin files have proper visibility modifiers.

### 4. Git Pre-Commit Hook

A Git pre-commit hook is available to automatically fix visibility issues before committing.

To install:
```bash
./install-git-hooks.sh
```

## Advanced Tools

### Kotlin Script: `kotlin-visibility-fixer.main.kts`

A more advanced Kotlin script that uses the Kotlin compiler API to analyze and fix visibility issues.

Usage:
```bash
./kotlin-visibility-fixer.main.kts --directory=/path/to/project [--dry-run]
```

### Gradle Plugin: `visibility-fixer-plugin`

A custom Gradle plugin that can be applied to any project to add visibility fixing capabilities.

## How It Works

These tools scan Kotlin files for declarations (classes, interfaces, functions, etc.) that lack explicit visibility modifiers and add `public` visibility where appropriate.

This helps satisfy the Kotlin explicit API mode requirements without having to manually add visibility modifiers to all declarations.
