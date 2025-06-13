// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1") // Matched with Kotlin 1.9.0
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.firebase:perf-plugin:1.4.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")
    }
}

plugins {
    id("com.android.application") version "8.10.1" apply false // Matched with Kotlin 1.9.0
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.5" apply false // CORRECTED
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false // Use the latest stable version of KSP
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8" // Common practice for Android, can be adjusted
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview"
            )
        }
    }
}
