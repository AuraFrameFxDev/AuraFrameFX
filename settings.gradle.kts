@file:Suppress("UnstableApiUsage")

pluginManagement {
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

        // Only Gradle plugins should be declared here, not regular dependencies

    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    // Using the incubating RepositoriesMode to ensure all repositories are declared in settings.gradle.kts
    @Suppress("DEPRECATION")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://api.xposed.info/") }
    }
}

rootProject.name = "AuraFrameFx"
include(":app")
