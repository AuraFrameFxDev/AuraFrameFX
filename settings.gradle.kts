@file:Suppress("UnstableApiUsage")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.namespace) {
                "com.android" -> useVersion("8.1.0")
                "org.jetbrains.kotlin" -> useVersion("1.9.22")
                "com.google.dagger.hilt.android" -> useVersion("2.48.1")
                "com.google.gms.google-services" -> useVersion("4.4.0")
                "com.google.firebase.crashlytics" -> useVersion("2.9.9")
                "com.google.firebase.firebase-perf" -> useVersion("1.4.2")
                "androidx.navigation.safeargs.kotlin" -> useVersion("2.7.7")
                "com.google.devtools.ksp" -> useVersion("1.9.22-1.0.16")
                "org.openapi.generator" -> useVersion("7.3.0")
            }
        }
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
    plugins {
        id("org.jetbrains.compose") version "1.6.11"
        id("org.openapi.generator") version "7.5.0"
        id("androidx.navigation.safeargs.kotlin") version "2.7.7" // Added for safeargs kotlin
        // Removed invalid plugin entries which were actually libraries
        // androidx.navigation.navigation-safe-args-gradle-plugin will be handled by root build.gradle.kts

        maven { url = uri("https://androidx.dev/storage/compose-compiler/repository/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }

    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { url = uri("https://androidx.dev/storage/compose-compiler/repository/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }
}

rootProject.name = "AuraFrameFx"
include(":app")