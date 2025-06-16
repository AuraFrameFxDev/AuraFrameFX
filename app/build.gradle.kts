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
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.ksp)
}

// Repositories are configured in settings.gradle.kts

// Common versions
val kotlinVersion = libs.versions.kotlin.get()
val composeVersion = "1.6.7" // Compatible with Kotlin 2.1.23
val composeBomVersion = composeVersion // Use composeVersion for BOM
val composeCompilerExtensionVersion = composeVersion // Use composeVersion for compiler extension
val hiltVersion = "2.56.2"
val navigationVersion = "2.7.5"
val firebaseBomVersion = "32.7.0"
val lifecycleVersion = "2.9.1"
android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

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
        kotlinCompilerExtensionVersion = composeCompilerExtensionVersion
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")

    // Hilt for dependency injection
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:${libs.versions.hilt.get()}")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Xposed Framework (local jar, since remote repo is unavailable)
    // compileOnly(files("libs/xposed-api-82.jar"))

    // LSPosed specific
    compileOnly("org.lsposed.hiddenapibypass:hiddenapibypass:6.1") {
        exclude(group = "de.robv.android.xposed", module = "api")
    }

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Exclude firebase-common if needed
    configurations.all {
        exclude(group = "com.google.firebase", module = "firebase-common")
    }

    // Firebase ML Kit
    implementation("com.google.firebase:firebase-ml-modeldownloader-ktx")

    // ML Kit
    implementation("com.google.mlkit:language-id:17.0.6")
    implementation("com.google.mlkit:translate:17.0.3")

    // TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:2.14.0") {
        exclude(group = "org.tensorflow", module = "tensorflow-lite-api")
    }
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4") {
        exclude(group = "org.tensorflow", module = "tensorflow-lite-support-api")
    }
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-text:0.4.4")

    // Accompanist for Compose utilities (version 0.32.0 is compatible with Compose 1.5.4)
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation("com.google.accompanist:accompanist-pager:0.32.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.32.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.32.0")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")
    implementation("com.google.accompanist:accompanist-webview:0.32.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.32.0")
    implementation("com.google.accompanist:accompanist-placeholder-material:0.32.0")
    implementation("com.google.accompanist:accompanist-navigation-material:0.32.0")

    // Room for local database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:$navigationVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Retrofit for network calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("app.cash.turbine:turbine:1.2.1")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.0")

    // Hilt testing
    kaptTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    testImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")

    // AndroidX Test
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // Debug implementations
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Desugar JDK libs for Java 8+ APIs on older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
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
