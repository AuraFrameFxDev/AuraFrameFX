import org.gradle.internal.declarativedsl.intrinsics.gradleRuntimeIntrinsicsKClass

val kotlinVersion = libs.versions.kgp.get() // Using kgp for Kotlin Gradle Plugin version
val agpVersion = libs.versions.agp.get()
val googleServicesVersion = libs.versions.googleServices.get()
val firebaseCrashlyticsVersion = libs.versions.firebaseCrashlytics.get()
val firebasePerformanceVersion = libs.versions.firebasePerf.get() // Corrected alias
val hiltVersion = libs.versions.hilt.get()
val kspVersion = libs.versions.ksp.get()
val navigationVersion = libs.versions.navigationSafeArgs.get() // Corrected alias
val dokkaVersion = libs.versions.dokka.get()


// This 'plugins' block applies plugins to your *project*.
// All plugin versions are now managed by libs.versions.toml and applied via alias().
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.library) // Added for library modules if you ever create them
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose) // org.jetbrains.compose plugin
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
    alias(libs.plugins.ktlint) // Ensure this is also applied here if defined in libs.versions.toml
    alias(libs.plugins.kotlin.compose.compiler) // Add this if you want it applied project-wide
}

// Common configurations for all projects
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://api.xposed.info/") }
    }
    // Consolidated Kotlin compilation options (removed duplicate blocks)
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions { // This block is still 'kotlinOptions' in allprojects context
            jvmTarget = JavaVersion.VERSION_21.toString() // Set to 21 for Java 21 toolchain
            languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9.version
            apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9.version
            freeCompilerArgs.addAll(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=kotlin.time.ExperimentalTime",
                "-opt-in=kotlin.experimental.ExperimentalTypeInference",
                "-opt-in=kotlin.ExperimentalStdlibApi",
                "-opt-in=kotlin.concurrent.ExperimentalAtomicApi",
                "-opt-in=kotlin.experimental.ExperimentalNativeApi"
            )
        }
    }
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    configurations.all {
        resolutionStrategy {
            val kotlinCoreVersion = libs.versions.kgp.get() // Using kgp for Kotlin standard library
            force("org.jetbrains.kotlin:kotlin-stdlib:$kotlinCoreVersion")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinCoreVersion")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinCoreVersion")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinCoreVersion")
            force("org.jetbrains.kotlin:kotlin-reflect:$kotlinCoreVersion")
        }
    }
}

// Global tasks (clean, Dokka, Detekt, Spotless)
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JavaVersion.VERSION_21.toString() // Use Java 21
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(false)
    }
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
    buildUponDefaultConfig = true
    ignoreFailures = false
    parallel = true
    autoCorrect = true
    debug = true
    source = files("src/main/java", "src/main/kotlin")
    include("**/*.kt", "**/*.java")
    exclude("**/build/**", "**/generated/**", "**/test/**", "**/androidTest/**")
}

tasks.withType<DokkaTask>().configureEach {
    outputFormat = "html"
    outputDirectory = "$rootDir/docs"
    dokkaSourceSets {
        configureEach {
            displayName.set("Main Sources")
            moduleName.set("AuraFrameFX")
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(uri("https://github.com/aurakai/AuraFrameFX/tree/main/app/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }
            sourceRoots.from(file("src/main/kotlin"))
            sourceRoots.from(file("src/main/java"))
            includeNonPublic.set(false)
            skipEmptyPackages.set(true)
            reportUndocumented.set(true)
            jdkVersion.set(9) // Can be set to 11 or 17 or 21 depending on specific Dokka needs
            languageVersion.set("1.9")
            apiVersion.set("1.9")
        }
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint(libs.versions.ktlint.get()).userData(mapOf("android" to "true"))
        licenseHeaderFile("$rootDir/config/spotless/copyright.kt")
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint(libs.versions.ktlint.get()).userData(mapOf("android" to "true"))
        licenseHeaderFile("$rootDir/config/spotless/copyright.kt")
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("misc") {
        target("**/*.md", "**/.gitignore")
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile("$rootDir/config/spotless/copyright.txt")
    }
}

val kspVersion = libs.versions.ksp.get() // Use ksp for gradleRuntimeIntrinsicsKClass