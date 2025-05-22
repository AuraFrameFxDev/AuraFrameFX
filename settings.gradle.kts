pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    
    plugins {
        id("com.android.application") version "8.1.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.22" apply false
        id("com.google.dagger.hilt.android") version "2.48" apply false
        id("com.google.gms.google-services") version "4.4.1" apply false
        id("com.google.firebase.crashlytics") version "2.9.9" apply false
        id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
    }
    
    resolutionStrategy {
        eachPlugin {
            when (requested.id.namespace) {
                "com.android" -> useModule("com.android.tools.build:gradle:${requested.version}")
                "com.google.dagger.hilt" -> useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://dl.google.com/dl/android/maven2") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

rootProject.name = "auraframe"
include(":app")