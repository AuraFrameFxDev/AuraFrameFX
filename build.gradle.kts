// Top-level build file where you can add configuration options common to all sub-projects/modules.
@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage", "DEPRECATION")

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.openapitools:openapi-generator-gradle-plugin:7.6.0")
    }
}

// Xposed JAR files configuration
val xposedApiJar = files("libs/api-82.jar")
val xposedBridgeJar = files("libs/bridge-82.jar")
val xposedApiSourcesJar = files("libs/api-82-sources.jar")
val xposedBridgeSourcesJar = files("libs/bridge-82-sources.jar")

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    id("org.openapi.generator") version "7.6.0" apply false
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "21"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=kotlin.time.ExperimentalTime",
                "-opt-in=kotlin.ExperimentalStdlibApi",
                "-opt-in=kotlin.concurrent.ExperimentalAtomicApi",
                "-opt-in=kotlin.experimental.ExperimentalNativeApi",
                "-Xcontext-receivers"
            )
        }
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