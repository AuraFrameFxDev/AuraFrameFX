// AuraFrameFxBeta/build.gradle.kts
// This entire 'buildscript' block MUST be at the very top of your file.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // These are the plugins/dependencies *for the buildscript itself*.
        // Their versions are hardcoded here. It's crucial they match your libs.versions.toml for consistency,
        // but for initial build stability, ensure these specific versions are correct and compatible.
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22") // Keep 1.9.22 for buildscript
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.firebase:perf-plugin:1.4.2")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.56.2")
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.9.22-1.0.16") // KSP must match Kotlin version
        classpath("androidx.navigation.safeargs:androidx.navigation.safeargs.gradle.plugin:2.9.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
    }
}

// Any 'val' declarations that use 'libs.versions.kotlin.get()' etc. can be placed here.
// These are defined *after* the buildscript block but *before* the allprojects block.
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
        import org.jetbrains.dokka.gradle.DokkaTask
        import io.gitlab.arturbosch.detekt.Detekt
        import java.io.File

val kotlinVersion = libs.versions.kotlin.get()
val agpVersion = libs.versions.agp.get()
val googleServicesVersion = libs.versions.googleServices.get()
val firebaseCrashlyticsVersion = libs.versions.firebaseCrashlytics.get()
val firebasePerformanceVersion = libs.versions.firebasePerformance.get()
val hiltVersion = libs.versions.hilt.get()
val kspVersion = libs.versions.ksp.get()
val navigationVersion = libs.versions.navigation.get()
val dokkaVersion = libs.versions.dokka.get()

// This 'plugins' block applies plugins to your *project*. It comes AFTER 'buildscript'.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false // Keep kapt for now
    alias(libs.plugins.kotlin.serialization) apply false

    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.spotless) apply false

    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false

    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false // Keep ksp

    alias(libs.plugins.navigation.safe.args) apply false

    alias(libs.plugins.dokka) // Apply Dokka plugin using alias (no apply false here if applied at root)
}

// Common configurations for all projects
allprojects {
    // ... (Your existing allprojects block contents go here)
    // Pay attention to any nested 'plugins' or 'dependencies' blocks within subprojects if they cause issues.
    // Ensure that inside 'allprojects' and 'subprojects', you are using 'apply(plugin = "...")' for plugins
    // as per your previous setup, and NOT 'classpath()' or 'alias()'.
}

// ... (Rest of your build.gradle.kts file)