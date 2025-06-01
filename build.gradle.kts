// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // Temporary workaround for Gradle 8.0+
plugins {
    // Core plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.kapt) apply false

    // Code Quality Plugins
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false

    // Firebase plugins
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false

    // Dependency Injection
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false

    // Navigation
    alias(libs.plugins.navigation.safe.args) apply false
}

// Configure Java toolchain for all projects
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = libs.versions.jvmTarget.get()
            // Enable experimental coroutines API
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xjvm-default=all"
            )
        }
    }
}

// Common configurations for all projects
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    
    // Configure Java toolchain
    plugins.withType<JavaBasePlugin> {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmTarget.get()))
            }
        }
    }
}