// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    // Define version constants inside buildscript block for access within its scope
    val kotlinVersion = "1.9.22"
    val kspVersion = "1.9.22-1.0.17"
    val hiltVersion = "2.51.1"
    val googleServicesVersion = "4.4.2"
    val crashlyticsVersion = "3.0.2"
    val firebasePerfVersion = "1.4.2"
    
    // Make versions available to all modules by putting them in extra properties
    project.extra.apply {
        set("kotlinVersion", kotlinVersion)
        set("kspVersion", kspVersion)
        set("hiltVersion", hiltVersion)
        set("googleServicesVersion", googleServicesVersion)
        set("crashlyticsVersion", crashlyticsVersion)
        set("firebasePerfVersion", firebasePerfVersion)
    }

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:google-services:$googleServicesVersion")
        classpath("com.google.firebase:firebase-crashlytics-gradle:$crashlyticsVersion")
        classpath("com.google.firebase:perf-plugin:$firebasePerfVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kspVersion")

    }
}

// These plugin declarations only make the plugins available to subprojects, they don't apply them to the root project
// Using a more traditional syntax to avoid dependency on version catalog in root project
plugins {
    id("com.android.application").version("8.4.0").apply(false)
    id("org.jetbrains.kotlin.android").version("1.9.22").apply(false)
    id("com.google.devtools.ksp").version("1.9.22-1.0.17").apply(false)
    id("com.google.dagger.hilt.android").version("2.51.1").apply(false)
    id("org.jetbrains.kotlin.plugin.serialization").version("1.9.22").apply(false)
    id("org.jetbrains.kotlin.plugin.parcelize").version("1.9.22").apply(false)
    id("com.google.gms.google-services").version("4.4.2").apply(false)
    id("com.google.firebase.crashlytics").version("3.0.2").apply(false)
    id("com.google.firebase.firebase-perf").version("1.4.2").apply(false)
    id("androidx.navigation.safeargs.kotlin").version("2.9.0").apply(false)
    id("org.jetbrains.compose").version("1.6.11").apply(false)
    id("org.openapi.generator").version("7.8.0").apply(false)
    // Additional plugins can be added here as needed
}

// Clean task to delete the build directory
tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
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
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
            force("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            // Retrofit to a specific version
            force("com.squareup.retrofit2:retrofit:2.9.0")
        }
    }
}

// Better approach for handling Gradle warnings
gradle.startParameter.warningMode = WarningMode.All