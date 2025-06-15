plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.firebase-perf")
    alias(libs.plugins.kotlin.compose)
}

// Repositories are configured in settings.gradle.kts

// Common versions
val composeBomVersion = "2025.06.00"
val composeCompilerVersion = "1.5.8" // This should match the Kotlin version
val composeVersion = "1.6.7" // This should match the BOM version
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
        kotlinCompilerExtensionVersion = composeCompilerVersion
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
    implementation("com.google.dagger:hilt-android:2.56.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")

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
    compileOnly("de.robv.android.xposed:api:82")
    compileOnly("de.robv.android.xposed:api:82:sources")

    // LSPosed specific
    compileOnly("org.lsposed.hiddenapibypass:hiddenapibypass:6.1") {
        exclude(group = "de.robv.android.xposed", module = "api")
    }

    // For Xposed API
    compileOnly("androidx.annotation:annotation:1.9.1")
    compileOnly("androidx.core:core:1.16.0")

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
    implementation("org.tensorflow:tensorflow-lite:2.17.0")

    // Accompanist for Compose utilities (version 0.32.0 is compatible with Compose 1.5.4)
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
    implementation("com.google.accompanist:accompanist-pager:0.36.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.36.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.36.0")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")
    implementation("com.google.accompanist:accompanist-webview:0.36.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.36.0")
    implementation("com.google.accompanist:accompanist-placeholder-material:0.36.0")
    implementation("com.google.accompanist:accompanist-navigation-material:0.36.0")

    // Room for local database
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    
    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.datastore:datastore-preferences-core:1.1.7")
    
    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.10.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:$navigationVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    
    // Kotlin serialization runtime
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Retrofit for network calls
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // SLF4J logging dependencies for LoggerFactory error
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.1.21")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("app.cash.turbine:turbine:1.2.1")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

    // Hilt testing
    kaptTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    testImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")

    // AndroidX Test
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.8.2")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("io.mockk:mockk-android:1.14.2")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // Debug implementations
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.8.2")

    // Desugar JDK libs for Java 8+ APIs on older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // Kotlinx datetime for Instant and Clock
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}

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
