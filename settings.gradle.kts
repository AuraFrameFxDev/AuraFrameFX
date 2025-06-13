// settings.gradle.kts

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
    plugins {
        id("com.android.application") version "8.14.2" // <--- ADD THIS LINE
        id("org.jetbrains.compose") version "1.6.11"
        id("org.openapi.generator") version "7.5.0"
        // ... other plugins
    }
}
