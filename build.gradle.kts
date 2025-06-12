// Top-level build file where you can add configuration options common to all sub-projects/modules.
// buildscript {} block and project.extra {} removed as versions are now managed by libs.versions.toml

// These plugin declarations make the plugins available to subprojects
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false // Corrected alias
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

// Add dependency resolutions for consistent dependency versions
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
            force(libs.kotlinx.serialization.core)  // Assumes this is the intended alias from TOML for the core library
            force(libs.kotlinx.serialization.json)  // Assumes this is the intended alias from TOML

            // Force consistent Compose versions
            force(libs.androidx.compose.compiler.lib)
            force(libs.androidx.compose.runtime.lib)
            force(libs.androidx.compose.foundation.lib)
            force(libs.androidx.compose.material3.lib) // Ensure this alias exists and is correct
            force(libs.androidx.compose.ui.lib)
            force(libs.androidx.compose.ui.tooling.lib)
            force(libs.androidx.compose.ui.tooling.preview.lib)
            
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
            apiVersion = "1.9"
            languageVersion = "1.9"
        }
    }
    
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
    }
}

// Better approach for handling Gradle warnings
gradle.startParameter.warningMode = org.gradle.api.logging.configuration.WarningMode.All