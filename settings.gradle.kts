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
        id(libs.plugins.android.application.get().pluginId) version libs.versions.agp.get() apply false
        id(libs.plugins.android.library.get().pluginId) version libs.versions.agp.get() apply false
        id(libs.plugins.kotlin.android.get().pluginId) version libs.versions.kotlin.get() apply false
        id(libs.plugins.kotlin.kapt.get().pluginId) version libs.versions.kotlin.get() apply false
        id(libs.plugins.kotlin.serialization.get().pluginId) version libs.versions.kotlin.get() apply false

        // Google Services and Firebase
        id(libs.plugins.google.services.get().pluginId) version libs.versions.googleServices.get() apply false
        id(libs.plugins.firebase.crashlytics.get().pluginId) version libs.versions.firebaseCrashlytics.get() apply false
        id(libs.plugins.firebase.perf.get().pluginId) version libs.versions.firebasePerformance.get() apply false

        // Hilt
        id(libs.plugins.hilt.android.get().pluginId) version libs.versions.hilt.get() apply false

        // KSP
        id(libs.plugins.ksp.get().pluginId) version libs.versions.ksp.get() apply false

        // Code quality
        id(libs.plugins.spotless.get().pluginId) version libs.versions.spotless.get() apply false
        id(libs.plugins.detekt.get().pluginId) version libs.versions.detekt.get() apply false
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