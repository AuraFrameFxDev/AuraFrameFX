enableFeaturePreview("VERSION_CATALOGS")
val kotlinPluginVersionForSettings: String = settings.providers.gradleProperty("kotlinPluginVersion").get()
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}
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
            when (requested.id.id) {
                "org.jetbrains.kotlin.android",
                "org.jetbrains.kotlin.kapt",
                "org.jetbrains.kotlin.plugin.serialization" -> {
                    useVersion(kotlinPluginVersionForSettings)
                }
            }
        }
    }
}

rootProject.name = "auraframefx"
include(":app")