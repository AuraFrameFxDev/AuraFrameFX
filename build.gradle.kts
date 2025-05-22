// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath("com.google.firebase:perf-plugin:1.4.2")  // Keep this for future use
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.diffplug.spotless") version "6.12.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
}

// Common configurations are now in settings.gradle.kts
allprojects {

    // Configure KtLint
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        android.set(true)
        ignoreFailures.set(true)  // Set to true to prevent build failures from KtLint
        filter {
            exclude { it.file.path.contains("build/") }
        }
    }
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    
    spotless {
        kotlin {
            target("**/*.kt")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
        }
        
        kotlinGradle {
            target("*.gradle.kts")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    // Configure Detekt
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        toolVersion = "1.23.0"
        config.setFrom(files("${project.rootDir}/detekt-config.yml"))
        buildUponDefaultConfig = true
    }
}

// Clean task is provided by the base plugin

// Simple documentation task for basic project documentation
tasks.register<DefaultTask>("docs") {
    group = "documentation"
    description = "Generate basic project documentation"

    doLast {
        val docsDir = file("${project.buildDir}/docs")
        docsDir.mkdirs()

        // Create a simple README if it doesn't exist
        val readmeFile = file("${project.rootDir}/README.md")
        if (!readmeFile.exists()) {
            readmeFile.writeText("# ${project.name}\n\nProject documentation will be generated here.")
        }

        println("Documentation generated at: ${docsDir.absolutePath}")
    }
}