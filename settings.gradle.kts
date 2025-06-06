pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }

    resolutionStrategy {
        eachPlugin {
            val pluginId = requested.id.id
            when {
                pluginId.startsWith("com.android.") -> useVersion("8.10.1")
                pluginId.startsWith("org.jetbrains.kotlin.") -> useVersion("2.1.21")
                pluginId == "com.google.devtools.ksp" -> useVersion("2.1.21-2.0.1")
                pluginId == "com.google.dagger.hilt.android" -> useVersion("2.50")
                pluginId == "com.google.gms.google-services" -> useVersion("4.4.1")
                pluginId == "com.google.firebase.crashlytics" -> useVersion("2.9.9")
                pluginId == "com.google.firebase.firebase-perf" -> useVersion("1.4.2")
                pluginId == "androidx.navigation.safeargs.kotlin" -> useVersion("2.7.7")
                pluginId == "org.jetbrains.dokka" -> useVersion("1.9.20")
                pluginId == "org.openapi.generator" -> useVersion("7.4.0")
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
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        // LSPosed API repository
        maven { url = uri("https://api.lsposed.org/repository/maven-public/") }
    }
    versionCatalogs {
        create("libs")
    }
}

rootProject.name = "auraframefx"

include(":app")

include(":lib")