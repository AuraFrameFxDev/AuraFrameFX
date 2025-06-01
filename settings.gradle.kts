pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://dl.google.com/dl/android/maven2") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.kotlin.android" ||
                requested.id.id == "org.jetbrains.kotlin.kapt" ||
                requested.id.id == "org.jetbrains.kotlin.plugin.serialization"
            ) {
                // Configuration if needed
            }
        }
    }
    plugins {
        kotlin("jvm") version "2.0.21"
    }
}

dependencyResolutionManagement {

    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "auraframefx"
include(":app")