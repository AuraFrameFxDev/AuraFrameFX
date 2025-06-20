plugins {
    `kotlin-dsl`
    // kotlin("jvm") version "1.9.10" // Not needed if only .gradle.kts files are present
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

// Match versions with root build.gradle.kts
val kotlinVersion = "1.9.10" // Downgraded
val agpVersion = "8.1.0"
val hiltVersion = "2.48"
val navVersion = "2.7.0"

dependencies {
    compileOnly(kotlin("gradle-plugin", version = kotlinVersion)) // Uses Downgraded kotlinVersion
    compileOnly(kotlin("gradle-plugin-api", version = kotlinVersion)) // Uses Downgraded kotlinVersion
    
    // Dependencies on Gradle plugins themselves should not be 'implementation' here
    // if they are managed by pluginManagement in settings.gradle.kts for the main build.
    // This can cause "plugin already on classpath" errors.
    // If buildSrc needs to reference types from these plugins for custom plugin development,
    // 'compileOnly' might be appropriate, or applying them by ID if buildSrc defines convention plugins.

    // // Android Gradle Plugin
    // implementation("com.android.tools.build:gradle:$agpVersion")
    //
    // // Hilt
    // implementation("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    //
    // // Navigation
    // implementation("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    //
    // // Other common plugins
    // implementation("com.google.gms:google-services:4.4.0")
    // implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion") // Uses Downgraded kotlinVersion
}

// Configure the Kotlin compiler
kotlin {
    jvmToolchain(17) // Toolchain version for compiling buildSrc code
}

// Explicitly configure Java toolchain for buildSrc itself
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
