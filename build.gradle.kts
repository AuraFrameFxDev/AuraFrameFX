// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    // Define version constants inside buildscript block for access within its scope
    val kotlinVersion = "1.9.22"  // Standardized on 1.9.22 for compatibility with KSP 1.9.22-1.0.17
    val kspVersion = "1.9.22-1.0.17"
    val hiltVersion = "2.56.2"    // Updated to latest version compatible with Kotlin 1.9.22
    val googleServicesVersion = "4.4.2"
    val crashlyticsVersion = "2.9.9"
    val firebasePerfVersion = "1.4.2"
    val agpVersion = "8.10.1"      // Android Gradle Plugin version
    val composeCompilerVersion = "1.5.14" // Stable for Kotlin 1.9.x

    // Make versions available to all modules by putting them in extra properties
    project.extra.apply {
        set("kotlinVersion", kotlinVersion)
        set("kspVersion", kspVersion)
        set("hiltVersion", hiltVersion)
        set("googleServicesVersion", googleServicesVersion)
        set("crashlyticsVersion", crashlyticsVersion)
        set("firebasePerfVersion", firebasePerfVersion)
        set("agpVersion", agpVersion)
        set("composeCompilerVersion", composeCompilerVersion)
    }

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$agpVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:google-services:$googleServicesVersion")
        classpath("com.google.firebase:firebase-crashlytics-gradle:$crashlyticsVersion")
        classpath("com.google.firebase:perf-plugin:$firebasePerfVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5") // Downgraded to be compatible with AGP 8.2.2
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kspVersion")
    }
}

// These plugin declarations only make the plugins available to subprojects, they don't apply them to the root project
plugins {
    id("com.android.application").version(rootProject.extra["agpVersion"] as String).apply(false)
    id("org.jetbrains.kotlin.android").version(rootProject.extra["kotlinVersion"] as String)
        .apply(false)
    id("com.google.devtools.ksp").version(rootProject.extra["kspVersion"] as String).apply(false)
    id("com.google.dagger.hilt.android").version(rootProject.extra["hiltVersion"] as String)
        .apply(false)
    id("org.jetbrains.kotlin.plugin.serialization").version(rootProject.extra["kotlinVersion"] as String)
        .apply(false)
    id("org.jetbrains.kotlin.plugin.parcelize").version(rootProject.extra["kotlinVersion"] as String)
        .apply(false)
    id("com.google.gms.google-services").version(rootProject.extra["googleServicesVersion"] as String)
        .apply(false)
    id("com.google.firebase.crashlytics").version(rootProject.extra["crashlyticsVersion"] as String)
        .apply(false)
    id("com.google.firebase.firebase-perf").version(rootProject.extra["firebasePerfVersion"] as String)
        .apply(false)
    id("androidx.navigation.safeargs.kotlin").version("2.7.5")
        .apply(false) // Downgraded to be compatible with AGP 8.2.2
    id("org.jetbrains.compose").version("1.5.11")
        .apply(false) // Make sure this is compatible with Kotlin 1.9.22
    id("org.openapi.generator").version("7.2.0").apply(false)
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

            // Force consistent kotlinx libraries - using versions from GitHub repo catalog
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
            force("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")  // Updated to match version catalog
            force("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")  // Updated to match version catalog

            // XML serialization has a specific version compatibility issue
            force("org.jetbrains.kotlinx:kotlinx-serialization-xml:0.70.0") // Use compatible version

            // Retrofit to the correct version
            force("com.squareup.retrofit2:retrofit:2.9.0") // Reverted to widely available version
        }
    }
}

// Better approach for handling Gradle warnings
gradle.startParameter.warningMode = WarningMode.All
