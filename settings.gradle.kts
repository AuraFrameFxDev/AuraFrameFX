@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
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
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://api.xposed.info/") }
    }
    
    // Version catalog configuration - no need to explicitly declare 'libs' here
    // as it's automatically loaded from gradle/libs.versions.toml
    versionCatalogs {
        // This block is intentionally left empty as we're using the default 'libs' catalog
        // from gradle/libs.versions.toml
    }
}

rootProject.name = "AuraFrameFx"
include(":app")
