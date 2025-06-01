pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
    // Define plugin versions in one place
    plugins {
        // Android Gradle Plugin
        id("com.android.application") version("8.2.2")
        
        // Kotlin
        kotlin("android") version("2.0.0")
        kotlin("plugin.serialization") version("2.0.0")
        
        // Google Services
        id("com.google.gms.google-services") version("4.4.2")
        id("com.google.firebase.crashlytics") version("2.9.9")
        id("com.google.firebase.firebase-perf") version("1.4.2")
        
        // Hilt
        id("com.google.dagger.hilt.android") version("2.51.1")
        
        // KSP
        id("com.google.devtools.ksp") version("2.0.0-1.0.21")
        
        // Documentation
        id("org.jetbrains.dokka") version("2.0.0")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "auraframefx"
include(":app")