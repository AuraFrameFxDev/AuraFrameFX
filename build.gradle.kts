// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Define versions in a single place
    val kotlinVersion = "1.9.0"
    val agpVersion = "8.1.0"
    val hiltVersion = "2.48"
    val navVersion = "2.7.0"
    
    // Android and Kotlin plugins
    id("com.android.application") version agpVersion apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion apply false
    
    // Hilt
    id("com.google.dagger.hilt.android") version hiltVersion apply false
    
    // KSP (Kotlin Symbol Processing)
    id("com.google.devtools.ksp") version "${kotlinVersion}-1.0.13" apply false
    
    // Google Services and Firebase
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
    
    // Navigation
    id("androidx.navigation.safeargs.kotlin") version navVersion apply false
    
    // Other plugins
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("org.openapi.generator") version "7.5.0" apply false
}

// For buildscript classpath dependencies that can't be in the plugins block
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    
    dependencies {
        // Hilt
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        
        // Google Services
        classpath("com.google.gms:google-services:4.4.0")
        
        // Firebase
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.firebase:perf-plugin:1.4.2")
        
        // Navigation
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.0")
    }
}

// Custom task to fix Kotlin visibility issues
// tasks.register("fixKotlinVisibility") {
//     group = "build"
//     description = "Fixes Kotlin visibility issues for explicit API mode"
    
//     doLast {
//         val scriptPath = "${rootProject.projectDir}/fix-kotlin-visibility.sh"
        
//         // Make sure the script is executable
//         providers.exec {
//             commandLine("chmod", "+x", scriptPath)
//         }
        
//         // Run the script
//         providers.exec {
//             commandLine(scriptPath)
//         }
        
//         println("Kotlin visibility fixing completed")
//     }
// }

// Configure Java toolchain for all projects
allprojects {
    plugins.withType<JavaBasePlugin> {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
                vendor.set(JvmVendorSpec.AZUL)
            }
        }
    }
}

// Configure Kotlin compilation for all projects
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        // Ensure we're using Java 17 compatibility
        kotlinOptions {
            jvmTarget = "17"
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
    
    // Configure Java compilation for all projects
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }
}
