import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    dependencies {
        // Make sure all plugins are available in the buildscript classpath
        classpath(libs.plugins.agp.get())
        classpath(libs.plugins.kotlin.gradle)
        classpath(libs.plugins.google.services)
        classpath(libs.plugins.firebase.crashlytics)
        classpath(libs.plugins.firebase.perf)
        classpath(libs.plugins.hilt.android.gradle)
        classpath(libs.plugins.ksp)
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
    
    // Other plugins
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.navigation.safe.args) apply false
}

// Common configurations for all projects
allprojects {
    // Apply ktlint plugin to all projects
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    
    // Configure Kotlin standard library
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-reflect:${libs.versions.kotlin.get()}")
        }
    }
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-reflect:${libs.versions.kotlin.get()}")
        }
    }

    // Configure Kotlin compiler options
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf(
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

    // Configure repositories for all projects
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

        // Configure exclusive content for Google's Maven repository
        exclusiveContent {
            forRepository {
                google()
            }
            filter {
                includeGroupByRegex("com\\.google\\..*")
                includeGroupByRegex("androidx\\..*")
            }
        }

        // Configure exclusive content for Maven Central
        exclusiveContent {
            forRepository {
                mavenCentral()
            }
            filter {
                includeGroupByRegex("org\\.jetbrains\\..*")
                includeGroupByRegex("com\\.google\\.code\\.gson")
            }
        }
    }
}

// Configure spotless for code formatting
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Configure detekt for static code analysis
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        toolVersion = libs.versions.detekt.get()
        config = files("${project.rootDir}/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        autoCorrect = true
        parallel = true

        reports {
            html.required.set(true)
            xml.required.set(true)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }
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
tasks.register("docs", org.jetbrains.dokka.gradle.DokkaTask::class) {
    outputDirectory.set(file("${layout.buildDirectory.get()}/dokka"))
    dokkaSourceSets {
        named("main") {
            displayName.set("AuraFrameFx")
            sourceRoots.from("src/main/java")
            sourceRoots.from("src/main/kotlin")
        }
    }
}