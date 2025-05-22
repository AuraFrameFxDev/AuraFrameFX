plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 34
    // Use version catalog

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdkVersion(libs.versions.minSdk.get().toInt())
        targetSdk = 34
        // Use version catalog
        // Use version catalog
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Use Java 17 as defined in versions
        targetCompatibility = JavaVersion.VERSION_17 // Use Java 17 as defined in versions
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get() // Use version catalog
    }

    // Enable Compose features
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() // Use version catalog
    }
    buildToolsVersion = rootProject.extra["buildToolsVersion"] as String
}

dependencies {
    // AndroidX Core and Material Design (ensure Material Components are available for themes)
    implementation(libs.androidx.core.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1") // This was hardcoded and not in version catalog, keeping for now if explicitly needed. Consider moving to version catalog.
    implementation(libs.material) // Use Material Components from version catalog

    // Compose BOM for consistent Compose versions
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3) // Crucial for Material 3 themes and typography

    // Lifecycle and Navigation Compose
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Firebase (ensure you are using the firebase-bom platform for consistent versions)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging) // Explicitly added as it was in AndroidManifest
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    // Add firebase-auth if you use it in your code, it was in the prev app/build.gradle.kts
    // implementation("com.google.firebase:firebase-auth")

    // Vertex AI (ensure versions are aligned with your libs.versions.toml)
    implementation(libs.google.cloud.aiplatform)
    // google-cloud-storage was hardcoded, adding it for now. Consider moving to version catalog.
    implementation("com.google.cloud:google-cloud-storage:2.20.0")
    implementation(libs.google.generative.ai)
    implementation(libs.google.cloud.vertexai)
    implementation(libs.google.api.grpc.protoGoogleCloudAiplatformV1)


    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler) // Kapt for Hilt compiler
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    // Serialization & DateTime
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // Networking
    implementation(platform(libs.okhttp.bom)) // Use OkHttp BOM for consistent versions
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Image Loading & Lottie
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    // Other hardcoded dependencies from previous app/build.gradle.kts, consider moving to version catalog
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Already in libs.versions.toml, so should be implementation(libs.constraintlayout)
}

