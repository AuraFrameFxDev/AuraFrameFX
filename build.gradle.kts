buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.firebase:perf-plugin:1.4.2")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
        classpath("androidx.navigation.safeargs:androidx.navigation.safeargs.gradle.plugin:2.9.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
    }
}

plugins {
    id("com.android.application") apply false
    id("com.google.gms.google-services") apply false
    id("com.google.firebase.crashlytics") apply false
    id("com.google.firebase.firebase-perf") apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false // Using stable Hilt version
    id("com.google.devtools.ksp") version "1.9.0-1.0.11" apply false // Using stable KSP version
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    id("org.jetbrains.dokka") apply false
    id("org.openapi.generator") apply false
    id("org.jetbrains.compose") version "1.5.3" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Add resolution strategies for dependency conflicts
allprojects {
    configurations.all {
        resolutionStrategy {
            // Force specific versions of Kotlin standard library and related libs
            force("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.0")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.0")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
            force("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
            
            // Force compatible versions of kotlinx libraries
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
            force("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            
            // Downgrade Retrofit to a compatible version
            force("com.squareup.retrofit2:retrofit:2.9.0")
            
            // Add other problematic dependencies as needed
        }
    }
}

// Better approach for handling Gradle 10.0 warnings
gradle.startParameter.warningMode = WarningMode.All
