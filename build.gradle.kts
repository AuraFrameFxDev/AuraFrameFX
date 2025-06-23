// Top-level build file where you can add configuration options common to all sub-projects/modules.

@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage", "DEPRECATION")

// Xposed JAR files configuration
val xposedApiJar = files("libs/api-82.jar")
val xposedBridgeJar = files("libs/bridge-82.jar")
val xposedApiSourcesJar = files("libs/api-82-sources.jar")
val xposedBridgeSourcesJar = files("libs/bridge-82-sources.jar")

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1") // Matched with Kotlin 1.9.0
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.firebase:perf-plugin:1.4.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false // Corrected alias


subprojects {
    configurations.all {
        resolutionStrategy {
            // Force consistent Kotlin versions
            force(libs.kotlin.stdlib.lib)
            force(libs.kotlin.stdlib.common.lib)
            force(libs.kotlin.stdlib.jdk7.lib)
            force(libs.kotlin.stdlib.jdk8.lib)
            force(libs.kotlin.reflect.lib)

            // Force consistent kotlinx libraries
            force(libs.kotlinx.coroutines.core) // Assumes this is the intended alias from TOML
            force(libs.kotlinx.coroutines.core.jvm) // Reverted to direct library alias
            force(libs.kotlinx.coroutines.android) // Assumes this is the intended alias from TOML
            // force(libs.kotlinx.serialization.core)  // REMOVED to allow Gradle to resolve potential conflicts
            // force(libs.kotlinx.serialization.json)  // REMOVED to allow Gradle to resolve potential conflicts

            // Force consistent Compose versions - REMOVED to allow BOM to manage versions
            // force(libs.androidx.compose.compiler.lib)
            // force(libs.androidx.compose.runtime.lib)
            // force(libs.androidx.compose.foundation.lib)
            // force(libs.androidx.compose.material3.lib)
            // force(libs.androidx.compose.ui.lib)
            // force(libs.androidx.compose.ui.tooling.lib)
            // force(libs.androidx.compose.ui.tooling.preview.lib)
            
            // Retrofit to a specific version
            force(libs.retrofit.lib) // Ensure this alias exists and is correct
        }
    }
}

// Configure Java 23 toolchain for all projects (assuming this was a previous change and should be kept)
allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "21"
            apiVersion = "1.9" // Keep Kotlin API/Language version as is, only JVM target changes
            languageVersion = "1.9"
        }
    }
    
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
    }

}

// Xposed framework configuration - must be compileOnly as it's provided by the Xposed framework at runtime
val xposedCompileOnly = configurations.create("xposedCompileOnly")

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
    delete("$projectDir/build")
}

tasks.register("cleanOpenApiGenerated", Delete::class) {
    delete("$projectDir/app/build/generated")
}