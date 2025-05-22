buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0") // Hardcoded Android Gradle Plugin version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0") // Hardcoded Kotlin Gradle Plugin version
        classpath("com.google.gms:google-services:4.4.0") // Hardcoded Google Services version
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9") // Hardcoded Crashlytics version
        
        // Code Quality Plugins
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.0")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:11.6.1")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.10")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
    
    // Code Quality Plugins
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("org.jetbrains.dokka") version "1.9.10"
}

// Apply Detekt and KtLint to all projects
allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    
    ktlint {
        android = true
        ignoreFailures = false
        disabledRules = ["no-wildcard-imports"]
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
    }
    
    detekt {
        toolVersion = "1.23.0"
        config = files("${project.rootDir}/detekt-config.yml")
        buildUponDefaultConfig = true
        autoCorrect = true
        
        reports {
            html.required = true
            xml.required = true
            txt.required = true
            sarif.required = true
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Dokka documentation generation
tasks.register<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtmlMultiModule") {
    outputDirectory.set(buildDir.resolve("dokka"))
    moduleName.set("AuraFrameFx")
    
    subprojects.forEach { project ->
        if (project.plugins.hasPlugin("org.jetbrains.kotlin.android")) {
            dokkaSourceSets {
                named("main") {
                    moduleName.set(project.name)
                    sourceRoot(project.file("src/main/kotlin"))
                    sourceRoot(project.file("src/main/java"))
                }
            }
        }
    }
}