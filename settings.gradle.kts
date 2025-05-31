// AuraFrameFxBeta/settings.gradle.kts

// Top-level settings file where you can configure option names are constants declared in the com.android.build.api.dsl.
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
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }
        maven { url = uri("https://repo1.maven.org/maven2/") }
    }

    // Force Kotlin standard library versions (this block might not be strictly necessary here if handled in build.gradle.kts)
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.kotlin.android" ||
                requested.id.id == "org.jetbrains.kotlin.kapt" ||
                requested.id.id == "org.jetbrains.kotlin.plugin.serialization") {
                // This block can be empty or contain specific rules if needed.
            }
        }
    }

    // REMOVED: The problematic 'plugins { ... }' block from here.
    // This block was causing conflicts.
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Strict repository management
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

        exclusiveContent {
            forRepository { google() }
            filter {
                includeGroupByRegex("com\\.google\\..*")
                includeGroupByRegex("androidx\\..*")
                includeGroupByRegex("com\\.android\\..*")
            }
        }
    }
    // ADD THIS BLOCK TO EXPLICITLY DECLARE YOUR VERSION CATALOG
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "auraframe"
include(":app")