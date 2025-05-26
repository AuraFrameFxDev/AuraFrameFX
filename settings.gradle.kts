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
        id("com.android.application") version "8.1.0" apply false
        id("com.android.library") version "8.1.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.22" apply false
        id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false

        // Google Services and Firebase
        id("com.google.gms.google-services") version "4.4.1" apply false
        id("com.google.firebase.crashlytics") version "2.9.9" apply false
        id("com.google.firebase.firebase-perf") version "1.4.2" apply false

        // Hilt
        id("com.google.dagger.hilt.android") version "2.56.2" apply false

        // KSP
        id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false

        // Code quality
        id("com.diffplug.spotless") version "6.12.0" apply false
        id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
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