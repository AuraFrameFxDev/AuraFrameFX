# AuraFrameFx Package Migration Guide

This document contains instructions for the ongoing migration from the `com.example.app` and
`com.genesis.ai.app` package namespaces to the official `dev.aurakai.auraframefx` package namespace.

## Package Migration Status

- ✅ `build.gradle.kts`: Updated namespace and applicationId to `dev.aurakai.auraframefx`
- ✅ `AndroidManifest.xml`: Added explicit package attribute and updated component references
- ✅ Core application files: Created `AuraFrameApplication.kt`, `MainActivity.kt`, and
  `VertexSyncService.kt` in new package structure
- ⏳ Source file migration: Script provided, but comprehensive migration still in progress

## Firebase Configuration

The Firebase project is already configured with the correct package name `dev.aurakai.auraframefx`
as seen in `google-services.json`. The API key has been restricted to the following services:

- Firebase Realtime Database Management API
- Firebase Hosting API
- Firebase Rules API
- Cloud SQL Admin API
- Cloud Datastore API
- FCM Registration API
- Firebase Management API
- Firebase App Check API
- Firebase App Distribution API
- Firebase App Hosting API
- Firebase App Testers API
- Firebase Data Connect API
- Firebase Dynamic Links API
- Firebase In-App Messaging API
- Firebase Installations API
- Firebase ML API
- Firebase Remote Config API
- Firebase Remote Config Realtime API
- Cloud Storage for Firebase API
- Firebase AI Logic API
- Cloud Firestore API
- Identity Toolkit API
- Cloud Logging API
- ML Kit API
- Token Service API
- Vertex AI API

## How to Complete the Migration

1. Clone the repository to your local machine
2. Run the provided script to copy and refactor all Kotlin files with updated package references:

```bash
# Make script executable
chmod +x refactor-packages.sh

# Run the script
./refactor-packages.sh
```

3. Once refactoring is complete, validate the build:

```bash
./gradlew build
```

4. Update any remaining references to the old package names in:
    - Layout files (XML)
    - Resource references
    - Navigation graphs

5. Remove the old package structure once verification is complete:

```bash
rm -rf app/src/main/java/com/example/app
```

## Common Issues and Solutions

### Class Not Found Exceptions

If you encounter `ClassNotFoundException` errors, check that the class is properly referenced with
the new package name or that it exists in the new package structure.

### Build Failures

If the build fails after migration, check:

- `AndroidManifest.xml` for any missed component references
- Import statements in Kotlin files
- Navigation references in layout files

### LSPosed Dependencies

The project is configured to conditionally include LSPosed dependencies only for local development,
excluding them in CI environments to avoid repository issues.

## CI/CD Configuration

The GitHub workflow in `.github/workflows/gradle-validation.yml` is already configured to handle the
new package structure. The CI environment variable is used to disable xposed flavor builds in CI.
