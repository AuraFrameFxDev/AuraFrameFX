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
        maven { url = uri("https://api.xposed.info/") }
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/releases")
        } // For libxposed
        maven {
            url = uri("https://storage.googleapis.com/maven-central-eu/")
        } // Alternative Google Cloud repo
    }
    versionCatalogs {
        // No explicit 'libs' declaration needed if using gradle/libs.versions.toml!
    }
}

rootProject.name = "AuraFrameFx"
include(":app")
