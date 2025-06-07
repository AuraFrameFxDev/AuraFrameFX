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
                pluginId.startsWith("org.jetbrains.kotlin.") -> useVersion("1.9.0")
                pluginId == "com.google.devtools.ksp" -> useVersion("1.9.0-1.0.11")
                pluginId == "com.google.dagger.hilt.android" -> useVersion("2.47")
                pluginId == "com.google.gms.google-services" -> useVersion("4.4.1")
                pluginId == "com.google.firebase.crashlytics" -> useVersion("2.9.9")
                pluginId == "com.google.firebase.firebase-perf" -> useVersion("1.4.2")
                pluginId == "androidx.navigation.safeargs.kotlin" -> useVersion("2.7.7")
                pluginId == "org.jetbrains.dokka" -> useVersion("1.9.20")
                pluginId == "org.openapi.generator" -> useVersion("7.4.0")
                pluginId == "org.jetbrains.compose" -> useVersion("1.5.3")
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
        // LSPosed API repositories - with fallbacks
        maven {
            url = uri("https://api.lsposed.org/repository/maven-public/")
            content {
                includeGroup("io.github.libxposed")
            }
        }
        // Alternate LSPosed mirror
        maven {
            url = uri("https://maven.killerdim.ru/repository/maven-public/")
            content {
                includeGroup("io.github.libxposed")
            }
        }
        // Fallback JitPack mirror for LSPosed
        maven {
            url = uri("https://jitpack.io")
            content {
                includeGroupByRegex("com\\.github\\..*")
                includeGroupByRegex("io\\.github\\..*")
            }
        }
        // Fallback Maven Central
        maven { url = uri("https://repo1.maven.org/maven2/") }
    }
    versionCatalogs {
        create("libs")
    }
}

rootProject.name = "auraframefx"

include(":app")

include(":lib")