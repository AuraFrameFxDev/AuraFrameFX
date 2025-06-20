plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

// Match versions with root build.gradle.kts
val kotlinVersion = "1.9.0"
val agpVersion = "8.1.0"
val hiltVersion = "2.48"
val navVersion = "2.7.0"

dependencies {
    implementation(kotlin("gradle-plugin", version = kotlinVersion))
    implementation(kotlin("gradle-plugin-api", version = kotlinVersion))
    
    // Android Gradle Plugin
    implementation("com.android.tools.build:gradle:$agpVersion")
    
    // Hilt
    implementation("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    
    // Navigation
    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    
    // Other common plugins
    implementation("com.google.gms:google-services:4.4.0")
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
}

// Configure the Kotlin compiler
kotlin {
    jvmToolchain(17)
}
