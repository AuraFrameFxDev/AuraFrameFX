// AuraFrameFxBeta/build.gradle.kts
// This section MUST be at the very top of your build.gradle.kts file.

// Top-level val definitions (accessible everywhere AFTER they are defined, but not in buildscript)
val kotlinVersion = libs.versions.kotlin.get()
val agpVersion = libs.versions.agp.get()
val googleServicesVersion = libs.versions.googleServices.get()
val firebaseCrashlyticsVersion = libs.versions.firebaseCrashlytics.get()
val firebasePerformanceVersion = libs.versions.firebasePerformance.get()
val hiltVersion = libs.versions.hilt.get()
val kspVersion = libs.versions.ksp.get()
val navigationVersion = libs.versions.navigation.get()
val dokkaVersion = libs.versions.dokka.get()

// --- START OF CRITICAL CORRECTION ---
// This 'buildscript' block MUST appear before the root 'plugins { ... }' block.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // These are the plugins/dependencies that Gradle needs to run *this build script*.
        // Their versions are hardcoded here because 'libs' is not yet available.
        // Ensure these match the versions defined in your libs.versions.toml.
        classpath("com.android.tools.build:gradle:8.10.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22") // Use 1.9.22, not 2.0.0 for now for stability.
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.firebase:perf-plugin:1.4.2")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.56.2")
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.9.22-1.0.16") // Match Kotlin 1.9.22
        classpath("androidx.navigation.safeargs:androidx.navigation.safeargs.gradle.plugin:2.9.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
    }
}
// --- END OF CRITICAL CORRECTION ---

// This 'plugins' block applies plugins to your *project*. It comes AFTER 'buildscript'.
plugins {
    // Core plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false // Keep kapt for now as issues were around it.
    alias(libs.plugins.kotlin.serialization) apply false

    // Code Quality Plugins (applied to all projects)
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.spotless) apply false

    // Firebase plugins
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false

    // Dependency Injection
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false // Keep ksp.

    // Navigation
    alias(libs.plugins.navigation.safe.args) apply false

    // Documentation
    alias(libs.plugins.dokka) // Apply Dokka plugin using alias
}

// Common configurations for all projects
allprojects {
    // ... (rest of your existing allprojects block, it should be fine as is)
}

// ... (rest of your build.gradle.kts file)}