pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
    versionCatalogs {
        // Version catalog is defined in gradle/libs.versions.toml
    }
}

rootProject.name = "AuraFrameFx"
include(":app", ":oracledrive")
