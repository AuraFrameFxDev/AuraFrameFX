// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    // Define version constants
    val kotlinVersion = "1.9.22"
    val kspVersion = "1.9.22-1.0.17"
    val hiltVersion = "2.50"
    val googleServicesVersion = "4.4.1"
    val crashlyticsVersion = "2.9.9"
    
    // Make versions available to all modules
    project.extra.apply {
        set("kotlinVersion", kotlinVersion)
        set("kspVersion", kspVersion)
        set("hiltVersion", hiltVersion)
        set("googleServicesVersion", googleServicesVersion)
        set("crashlyticsVersion", crashlyticsVersion)
    }
    
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("com.google.gms:google-services:$googleServicesVersion")
        classpath("com.google.firebase:firebase-crashlytics-gradle:$crashlyticsVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}

// These plugin declarations make the plugins available to subprojects
plugins {
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

// Add dependency resolutions for consistent dependency versions
subprojects {
    configurations.all {
        resolutionStrategy {
            // Force consistent Kotlin versions
            force("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlinVersion"]}")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:${rootProject.extra["kotlinVersion"]}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${rootProject.extra["kotlinVersion"]}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${rootProject.extra["kotlinVersion"]}")
            force("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra["kotlinVersion"]}")

            // Force consistent kotlinx libraries
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
            force("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            
            // Force consistent Compose versions
            force("androidx.compose.compiler:compiler:1.5.14")
            force("androidx.compose.runtime:runtime:1.5.14")
            force("androidx.compose.foundation:foundation:1.5.14")
            force("androidx.compose.material3:material3:1.1.2")
            force("androidx.compose.ui:ui:1.5.14")
            force("androidx.compose.ui:ui-tooling:1.5.14")
            force("androidx.compose.ui:ui-tooling-preview:1.5.14")
            
            // Retrofit to a specific version
            force("com.squareup.retrofit2:retrofit:2.9.0")
        }
    }
}

// Configure Java 17 toolchain for all projects
allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
            apiVersion = "1.9"
            languageVersion = "1.9"
        }
    }
    
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }
}

// Better approach for handling Gradle warnings
gradle.startParameter.warningMode = org.gradle.api.logging.configuration.WarningMode.All