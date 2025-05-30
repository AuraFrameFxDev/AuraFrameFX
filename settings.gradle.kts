// Top-level settings file where you can configure option names are constants declared in the com.android.build.api.dsl package.
// Define plugin versions
pluginManagement {
    repositories {
        // Primary plugin repositories in order of priority
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://dl.google.com/dl/android/maven2") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
    }
    
    // Force Kotlin standard library versions
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.kotlin.android" ||
                requested.id.id == "org.jetbrains.kotlin.kapt" ||
                requested.id.id == "org.jetbrains.kotlin.plugin.serialization") {

            }
        }
    }

    // Plugin versions should be defined in version catalogs when possible
    // These are kept here for compatibility
    plugins {
        // Android and Kotlin
        alias(libs.plugins.android.application) apply false
        alias(libs.plugins.android.library) apply false
        alias(libs.plugins.kotlin.android) apply false
        alias(libs.plugins.kotlin.kapt) apply false
        alias(libs.plugins.kotlin.serialization) apply false

        // Google Services and Firebase
        alias(libs.plugins.google.services) apply false
        alias(libs.plugins.firebase.crashlytics) apply false
        alias(libs.plugins.firebase.perf) apply false

        // Hilt
        alias(libs.plugins.hilt.android) apply false

        // KSP
        alias(libs.plugins.ksp) apply false

        // Code quality
        alias(libs.plugins.spotless) apply false
        alias(libs.plugins.detekt) apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

        // Configure exclusive content for Google's Maven repository
        exclusiveContent {
            forRepository { google() }
            filter {
                includeGroupByRegex("com\\.google\\..*")
                includeGroupByRegex("androidx\\..*")
                includeGroupByRegex("com\\.android\\..*")
            }
        }
    }
}

rootProject.name = "auraframe"
include(":app")