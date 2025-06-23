plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.navigation.safeargs)
    // id("org.jetbrains.compose") REMOVED - Handled by composeOptions
    alias(libs.plugins.openapi.generator)

    alias(libs.plugins.ksp)
}

// Repositories are configured in settings.gradle.kts

// Common versions
val kotlinVersion = "2.0.0"
val composeVersion = "1.5.4" // Use a Compose version compatible with Kotlin 1.9.0

val hiltVersion = "2.56.2"
val navigationVersion = "2.9.0"
val firebaseBomVersion = "33.15.0"
val lifecycleVersion = "2.9.1"


android {
    // Configure Java 24 compatibility
    compileSdk = 34
    
    defaultConfig {
        targetSdk = 34
        minSdk = 34  // Required for LSPosed
    }
    
    compileOptions {
        // Enable Java 24 language features
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true  // For Java 8+ APIs on older Android versions
    }
    
    kotlinOptions {
        jvmTarget = "21"  // Match Java version
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xjvm-default=all",  // Enable all JVM default methods
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
    
    // Configure Java toolchain for consistent build environment
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    
    kotlin {
        jvmToolchain(24)
    }
    
    // NDK version is now managed by Android Gradle Plugin
    defaultConfig {
        ndk {
            // Filter ABIs to reduce APK size
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
        }

        externalNativeBuild {
            cmake {
                cppFlags("-std=c++17 -fexceptions -frtti")
                arguments(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_TOOLCHAIN=clang",
                    "-DANDROID_CPP_FEATURES=rtti exceptions",
                    "-DANDROID_ARM_NEON=TRUE",
                    "-DANDROID_PLATFORM=android-21"
                )
                version = "3.22.1"
            }
        }

        // Set minimum SDK version for LSPosed and modern Android features
        minSdk = 34

        // Set target and compile SDK versions to meet dependency requirements
        targetSdk = 36
        compileSdk = 36

        // Enable JNI debugging
        ndk.debugSymbolLevel = "FULL"
    }

    // External native build configuration
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
            buildStagingDirectory = file("${layout.buildDirectory.get()}/../.cxx")
        }
    }

    // Configure source sets for JNI libs
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/cpp/libs")
        }
    }

    // Enable prefab for native dependencies
    buildFeatures {
        prefab = true
    }

    // Configure build types
    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Add packaging options to exclude duplicate files
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/license.txt")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/NOTICE.txt")
            excludes.add("META-INF/notice.txt")
            excludes.add("META-INF/ASL2.0")
            excludes.add("META-INF/*.version")
            excludes.add("META-INF/proguard/*.pro")
        }
    }

    // Add compile options for Java 8
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
    namespace = "dev.aurakai.auraframefx"

    defaultConfig {
        testInstrumentationRunnerArguments += mapOf("clearPackageData" to "true")
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        // Enable multidex support
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            freeCompilerArgs.addAll(
                listOf(
                    "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                    "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-opt-in=kotlin.RequiresOptIn"
                )
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
        aidl = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()

    }

    lint {
        lintConfig = file("lint.xml")
    }
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    ndkVersion = "27.0.12077973"

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    constraints {
        implementation("org.tensorflow:tensorflow-lite-api:2.14.0") {
            because("Align all TensorFlow Lite API versions to 2.14.0")
        }
        implementation("org.tensorflow:tensorflow-lite-support-api:0.4.4") {
            because("Align all TensorFlow Lite Support API versions to 0.4.4")
        }
    }

    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.10.1")

    // Compose BOM (Bill of Materials) - manages all Compose library versions
    val composeBom = platform("androidx.compose:compose-bom:$composeBomVersion")
    implementation(composeBom)

    // Core Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-util")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.8.2")

    // Material3
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")

    // Integration with activities
    implementation("androidx.activity:activity-compose:1.10.1")

    // Integration with ViewModels and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.compose.runtime:runtime-livedata:1.8.2")

    // Animations
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.animation:animation-graphics")

    // Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(composeBom)

    // Accompanist for Compose utilities
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    // Hilt for dependency injection
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Xposed Framework
    // compileOnly("de.robv.android.xposed:api:82") {
    //     isTransitive = false
    // }
    // Xposed Framework - Local JARs (USER MUST ADD THESE TO app/libs/)
    compileOnly(files("libs/api-82.jar"))
    compileOnly(files("libs/bridge-82.jar"))


    // LSPosed specific
    compileOnly("org.lsposed.hiddenapibypass:hiddenapibypass:6.1") {
        exclude(group = "de.robv.android.xposed", module = "api")
    }

kotlin {
    jvmToolchain(21)
}

    // AndroidX dependencies for Xposed
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))
    implementation("com.google.firebase:firebase-analytics-ktx:22.4.0")
    implementation("com.google.firebase:firebase-crashlytics-ktx:19.4.4")
    implementation("com.google.firebase:firebase-perf-ktx:21.0.5")
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.1")

    // Exclude firebase-common if needed
    configurations.all {
        exclude(group = "com.google.firebase", module = "firebase-common")
    }

    // Firebase ML Kit
    implementation("com.google.firebase:firebase-ml-modeldownloader-ktx:25.0.1")

    // ML Kit
    implementation("com.google.mlkit:language-id:17.0.6")
    implementation("com.google.mlkit:translate:17.0.3")

    // TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:2.14.0") {
        exclude(group = "org.tensorflow", module = "tensorflow-lite-api")
    }
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4") {
        exclude(group = "org.tensorflow", module = "tensorflow-lite-support-api")
        // Exclude the older TFLite version it brings to avoid conflicts with the main 2.14.0
        exclude(group = "org.tensorflow", module = "tensorflow-lite")
    }
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4") {
        exclude(group = "org.tensorflow", module = "tensorflow-lite")
        exclude(group = "org.tensorflow", module = "tensorflow-lite-api")
        exclude(group = "org.tensorflow", module = "tensorflow-lite-support-api")
    }
    implementation("org.tensorflow:tensorflow-lite-task-text:0.4.4") {
        exclude(group = "org.tensorflow", module = "tensorflow-lite")
        exclude(group = "org.tensorflow", module = "tensorflow-lite-api")
        exclude(group = "org.tensorflow", module = "tensorflow-lite-support-api")
    }
    
    // Protocol Buffers and Netty
    implementation("com.google.protobuf:protobuf-java:3.25.5")
    implementation("commons-io:commons-io:2.14.0")
    implementation("io.netty:netty-codec-http2:4.1.100.Final")
    implementation("io.netty:netty-handler:4.1.118.Final")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78")
    implementation("io.netty:netty-common:4.1.118.Final")
    implementation("org.apache.commons:commons-compress:1.26.0")
    implementation(libs.guava)
    implementation("io.netty:netty-codec-http:4.1.118.Final")
    implementation(libs.google.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.xml)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Permissions (use Accompanist)
    implementation(libs.accompanist.permissions)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Google Cloud
    implementation(platform(libs.google.cloud.bom))
    implementation(libs.google.cloud.generativeai)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.junit)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // UI Components
    implementation(libs.androidx.cardview)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.systemuicontroller)

    // Compose Glance
    implementation(libs.glance.appwidget)
    implementation(libs.glance.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.crashlytics)

    // Google Cloud AI - using BOM for version management
    implementation("com.google.cloud:google-cloud-generativeai")

    // Timber
    implementation(libs.timber)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    // Xposed dependencies - using local JARs
    compileOnly(xposedApiJar)
    compileOnly(xposedBridgeJar)

    // Xposed hidden API bypass
    xposedCompileOnly(libs.xposed.hiddenapibypass)

    // For development and documentation
    compileOnly(xposedApiSourcesJar) // Only needed for development
    compileOnly(xposedBridgeSourcesJar) // Only needed for development

    // LSPosed API (if using LSPosed specific features)
    xposedCompileOnly("org.lsposed:libxposed:82")
    xposedCompileOnly("org.lsposed:libxposed:82:sources") // For development only

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.espresso.core)


// KSP and KAPT configuration
kapt {
    correctErrorTypes = true // KAPT is in maintenance mode, use KSP where possible
}

// Register a task to build a jar for Xposed/LSPosed modules after the Android plugin is configured
afterEvaluate {
    android.applicationVariants.all { variant ->
        if (variant.buildType.name == "release" || variant.buildType.name == "debug") {
            tasks.register(
                "buildXposedJar${variant.name.replaceFirstChar { it.uppercase() }}",
                Jar::class
            ) {
                archiveBaseName.set("app-xposed-${variant.name}")
                from(variant.javaCompileProvider.get().destinationDirectory)
                destinationDirectory.set(file("${'$'}buildDir/libs"))
            }
        }
        true // Fix: return Boolean as expected
    }
}
