// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.0")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:11.6.1")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.10")
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.0" apply false
    id("com.diffplug.spotless") version "6.12.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("org.jetbrains.dokka") version "1.9.10" apply false
}

val buildToolsVersion by extra("34.0.0")

// Apply Detekt and KtLint to all projects
allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    
    ktlint {
        android.set(true)
        ignoreFailures.set(false)
        filter {
            exclude { it.file.path.contains("build/") }
        }
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
    }
    
    detekt {
        toolVersion = "1.23.0"
        source = files("src/main/kotlin")
        config = files("${project.rootDir}/detekt-config.yml")
        buildUponDefaultConfig = true
        autoCorrect = true
        
        reports {
            html.required.set(true)
            xml.required.set(true)
            txt.required.set(true)
            sarif.required.set(true)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Dokka documentation generation
subprojects {
    apply(plugin = "org.jetbrains.dokka")
    
    tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
        dokkaSourceSets {
            configureEach {
                // Only process main source sets
                if (name == "main") {
                    moduleName.set(project.name)
                    moduleVersion.set(project.version.toString())
                    
                    // Add source directories
                    sourceRoots.from(file("src/main/kotlin"))
                    sourceRoots.from(file("src/main/java"))
                    
                    // Include Android sources
                    jdkVersion.set(11)
                    
                    // Documented packages
                    perPackageOption {
                        matchingRegex.set(".*")
                        suppress.set(false)
                    }
                    
                    // Android documentation
                    externalDocumentationLink {
                        url.set(uri("https://developer.android.com/reference/").toURL())
                    }
                    
                    // Include AndroidX documentation
                    externalDocumentationLink {
                        url.set(uri("https://developer.android.com/reference/androidx/packages").toURL())
                    }
                    
                    // Include Kotlin stdlib documentation
                    externalDocumentationLink {
                        url.set(uri("https://kotlinlang.org/api/latest/jvm/stdlib/").toURL())
                    }
                }
            }
        }
    }
}

// Multi-module documentation task
tasks.register<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>("dokkaHtmlMultiModule") {
    outputDirectory.set(file("${buildDir}/dokka"))
    
    // Add dependency on all subproject dokka tasks
    dependsOn(subprojects.map { it.tasks.matching { task -> task.name.startsWith("dokka") } })
    
    // Configure the multi-module output
    moduleName.set("AuraFrameFx")
    moduleVersion.set(project.version.toString())
    
    // Add documentation for the root project
    dokkaSourceSets {
        configureEach {
            sourceRoots.from(file("build.gradle.kts"))
            
            // Include README and other documentation
            includes.from("README.md")
            
            // Link to project documentation
            externalDocumentationLink {
                url.set(uri("https://developer.android.com/reference/").toURL())
            }
        }
    }
}