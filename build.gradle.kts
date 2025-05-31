// AuraFrameFxBeta/build.gradle.kts (Root Project)

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Core plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    
    // Code quality plugins
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.spotless) apply false
    
    // Google services
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    
    // Hilt
    alias(libs.plugins.hilt.android) apply false
    
    // KSP
    alias(libs.plugins.ksp) apply false
    
    // Navigation
    alias(libs.plugins.navigation.safe.args) apply false
    
    // Dokka
    alias(libs.plugins.dokka) apply false
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask // Import DokkaTask explicitly
import io.gitlab.arturbosch.detekt.Detekt // Import Detekt task type
import java.io.File // Import File for File.separator

// Define Kotlin version at the top-level where 'libs' is resolvable.
// This variable is used in 'allprojects' and 'tasks.register("docs")'.
// Note: These val definitions are NOT accessible in the 'buildscript' block directly.
val kotlinVersion = libs.versions.kotlin.get()
val agpVersion = libs.versions.agp.get()
val googleServicesVersion = libs.versions.googleServices.get()
val firebaseCrashlyticsVersion = libs.versions.firebaseCrashlytics.get()
val firebasePerformanceVersion = libs.versions.firebasePerformance.get()
val hiltVersion = libs.versions.hilt.get()
val kspVersion = libs.versions.ksp.get()
val navigationVersion = libs.versions.navigation.get()
val dokkaVersion = libs.versions.dokka.get()


// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        // Use hardcoded versions here, as variables defined outside 'buildscript' block are not in scope.
        // These versions should match the ones defined in libs.versions.toml.
        classpath("com.android.tools.build:gradle:8.1.0") // Literal version from libs.versions.toml agp
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22") // Literal version from libs.versions.toml kotlin
        classpath("com.google.gms:google-services:4.4.1") // Literal version from libs.versions.toml googleServices
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9") // Literal version from libs.versions.toml firebaseCrashlytics
        classpath("com.google.firebase:perf-plugin:1.4.2") // Literal version from libs.versions.toml firebasePerformance, corrected artifact ID
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.56.2") // Literal version from libs.versions.toml hilt
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.9.22-1.0.16") // Literal version from libs.versions.toml ksp, corrected artifact ID
        classpath("androidx.navigation.safeargs:androidx.navigation.safeargs.gradle.plugin:2.9.0") // Literal version from libs.versions.toml navigation
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20") // Literal version from libs.versions.toml dokka
    }
}

plugins {
    // Core plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false

    // Code Quality Plugins (applied to all projects)
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false

    // Other plugins (apply false as they are applied per module or explicitly later)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.navigation.safe.args) apply false

    // Dokka plugin needs to be applied explicitly at the root level if the 'docs' task is here.
    alias(libs.plugins.dokka) // Apply Dokka plugin using alias
}

// Common configurations for all projects
allprojects {
    // Apply ktlint plugin to all projects
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    // Configure Kotlin standard library
    configurations.all {
        resolutionStrategy {
            // Use the top-level 'kotlinVersion' val which is correctly resolved here.
            force("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
            force("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
        }
    }

    // Configure Kotlin compiler options
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs += listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
            )
        }
    }

    // Configure ktlint
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        ignoreFailures.set(true)
        filter {
            exclude { it.file.path.contains("build/") || it.file.path.contains("buildSrc/") }
            // Include only Kotlin and Java files
            include("**/*.kt")
            include("**/*.java")
        }
    }

    // Apply Detekt plugin and configure its extension for all subprojects
    apply(plugin = "io.gitlab.arturbosch.detekt")
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        toolVersion = libs.versions.detekt.get()
        config.setFrom(files("${project.rootDir}/config/detekt/detekt-config.yml"))
        buildUponDefaultConfig = true
        autoCorrect = true
        parallel = true
        // Removed deprecated `allRules`, `ignoreFailures`, `debug` from here
    }

    // Configure Detekt tasks for all projects/modules
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = JavaVersion.VERSION_17.toString()
        parallel = true
        ignoreFailures = false // Set at task level
        buildUponDefaultConfig = true

        // Use the config file from the root project
        config.setFrom(files("${project.rootDir}/config/detekt/detekt-config.yml")) // Use setFrom for task config

        // Correct way to set source, using project.files(...).asFileTree and filter directly
        setSource(project.files(
            "src/main/java",
            "src/main/kotlin",
            "src/test/java",
            "src/test/kotlin",
            "src/androidTest/java",
            "src/androidTest/kotlin"
        ).asFileTree.filter { file ->
            !file.path.contains("${File.separator}build${File.separator}") &&
                    !file.path.contains("${File.separator}generated${File.separator}") // Exclude generated code
        })

        // Configure reports
        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
            md.required.set(false)
        }
    }
}

// Consolidate 'clean' task to the root build.gradle.kts
tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory.get().asFile) // Use layout.buildDirectory.get().asFile for non-deprecated usage
}

// Custom tasks for Detekt and Check at the root level (if desired)
tasks.register("detektAll") {
    description = "Runs Detekt on all modules"
    group = "verification"
    dependsOn(subprojects.map { it.tasks.withType<io.gitlab.arturbosch.detekt.Detekt>() })
}

tasks.register("check") {
    description = "Runs all checks (including Detekt) across all modules."
    group = "verification"
    dependsOn(tasks.named("detektAll"))
    // Depend on the 'check' task of each subproject that has one (e.g., Android modules)
    dependsOn(subprojects.map { it.tasks.matching { task -> task.name == "check" } })
}


// Configure spotless for code formatting
configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        ktlint()
        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }

    format("misc") {
        target("*.gradle", "*.md", ".gitignore")
        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }
}

// Configure docs task to generate documentation
tasks.register("docs", DokkaTask::class) {
    outputDirectory.set(project.layout.buildDirectory.dir("dokka"))
    dokkaSourceSets {
        named("main") {
            displayName.set("AuraFrameFx")
            sourceRoots.from("src/main/java")
            sourceRoots.from("src/main/kotlin")
        }
    }
}