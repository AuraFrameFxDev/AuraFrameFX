// settings.gradle.kts

@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
    plugins {
        id("com.android.application") version libs.versions.agp.get()
        id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get()
        id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin.get()
        id("org.jetbrains.kotlin.plugin.parcelize") version libs.versions.kotlin.get()
        id("dagger.hilt.android.plugin") version libs.versions.hilt.get()
        id("com.google.gms.google-services") version libs.versions.googleServicesPlugin.get()
        id("androidx.navigation.safeargs.kotlin") version libs.versions.androidxNavigationSafeargs.get()
        id("org.jetbrains.compose") version libs.versions.composePlugin.get()
        id("org.openapi.generator") version libs.versions.openapiGeneratorPlugin.get()
        id("com.google.devtools.ksp") version libs.versions.ksp.get()
        id("com.google.firebase.crashlytics") version libs.versions.firebaseCrashlyticsPlugin.get()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    @Suppress("DEPRECATION")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://api.xposed.info/") }
    }
}

rootProject.name = "AuraFrameFx"
include(":app")

// The following blocks are retained for completeness based on your logaura.docx,
// but for a purely modern Gradle setup with proper pluginManagement,
// the buildscript block for plugin classpaths becomes redundant.
// Similarly, the subprojects and allprojects configurations might be simplified
// or moved into individual module build files if appropriate for your project structure.

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${libs.versions.agp.get()}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${libs.versions.hilt.get()}")
        classpath("com.google.gms:google-services:${libs.versions.googleServicesPlugin.get()}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${libs.versions.firebaseCrashlyticsPlugin.get()}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${libs.versions.androidxNavigationSafeargs.get()}")
    }
}

// These plugin declarations make the plugins available to subprojects
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kspPlugin) apply false
    alias(libs.plugins.hiltPlugin) apply false
    alias(libs.plugins.kotlinPluginSerialization) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

// Add dependency resolutions for consistent dependency versions
subprojects {
    configurations.all {
        resolutionStrategy {
            // Force consistent Kotlin versions
            force("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-reflect:${libs.versions.kotlin.get()}")

            // Force consistent kotlinx libraries
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
            force("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
            force("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            // Force consistent Compose versions
            force("androidx.compose.compiler:compiler:1.5.15")
            force("androidx.compose.material3:material3:1.3.2")
            force("androidx.compose.ui:ui-tooling-preview:1.8.2")
            
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
