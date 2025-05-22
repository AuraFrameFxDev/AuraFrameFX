plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp") version "1.9.22-1.0.16"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

// Read local.properties file
val localProperties = java.util.Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        freeCompilerArgs += "-Xjvm-default=all"
    }

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Enable BuildConfig generation
        buildConfigField("boolean", "DEBUG_MODE", "true")

        // Google Cloud configuration from local.properties
        val googleCloudProjectId = localProperties.getProperty("GOOGLE_CLOUD_PROJECT_ID", "")
        val googleCloudApiKey = localProperties.getProperty("GOOGLE_CLOUD_API_KEY", "")

        buildConfigField("String", "GOOGLE_CLOUD_PROJECT_ID", "\"$googleCloudProjectId\"")
        buildConfigField("String", "GOOGLE_CLOUD_API_KEY", "\"$googleCloudApiKey\"")

        // Add Google Cloud API key to manifest for network security config
        manifestPlaceholders["googleCloudApiKey"] = googleCloudApiKey

        // Secure configuration for Google Cloud services
        // buildConfigField("String", "GOOGLE_CLOUD_PROJECT_ID", "\"${project.findProperty("GOOGLE_CLOUD_PROJECT_ID") ?: ""}\"")
        // buildConfigField("String", "GOOGLE_CLOUD_API_KEY", "\"${project.findProperty("GOOGLE_CLOUD_API_KEY") ?: ""}\"")

        // Enable vector drawable support
        vectorDrawables {
            useSupportLibrary = true
        }
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
        buildConfig = true
    }

    // Include secure assets
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "app/secure")
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() // Use version catalog
    }

}

dependencies {
    // Kotlin Standard Library
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-common"))
    implementation(kotlin("stdlib-jdk8"))

    // AndroidX Core and Material Design
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // Compose BOM for consistent Compose versions
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Firebase (using BOM for consistent versions)
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))

    // Genesis Protocol
    implementation(platform("com.google.cloud:libraries-bom:26.30.0"))

    // Google Cloud Speech-to-Text
    implementation("com.google.cloud:google-cloud-speech:4.32.0") {
        exclude(group = "io.grpc", module = "grpc-netty-shaded")
    }

    // Google Cloud AI Platform
    implementation("com.google.cloud:google-cloud-aiplatform:3.37.0") {
        exclude(group = "io.grpc", module = "grpc-netty-shaded")
    }

    // Google Generative AI
    implementation("com.google.ai.generativeai:generativeai:0.3.1")
    
    // Kotlin Standard Library
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    
    // Dependency Injection
    implementation("javax.inject:javax.inject:1")
    implementation("com.google.dagger:dagger:2.48")
    ksp("com.google.dagger:dagger-compiler:2.48")
    
    // Vertex AI
    implementation(platform("com.google.cloud:libraries-bom:26.30.0"))
    implementation("com.google.cloud:google-cloud-vertexai")
    implementation("com.google.cloud:google-cloud-vertexai-http-java")
    implementation("com.google.cloud:google-cloud-aiplatform")
    implementation("com.google.api.grpc:proto-google-cloud-aiplatform-v1")
    implementation("com.google.cloud:google-cloud-storage")
    
    // Google Generative AI (for Vertex AI)
    implementation("com.google.ai.generativeai:generativeai:0.3.1") {
        exclude(group = "com.google.guava", module = "guava")
    }
    
    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Google Auth and API Client
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation("com.google.auth:google-auth-library-credentials:1.19.0")
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.apis:google-api-services-aiplatform:v1-rev20240311-2.0.0")

    // gRPC and Protobuf
    implementation(platform("io.grpc:grpc-bom:1.62.2"))
    implementation("io.grpc:grpc-okhttp")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("com.google.protobuf:protobuf-java:3.25.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.25.1")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // TensorFlow Lite for on-device ML
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-audio:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-text:0.4.4")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // AndroidX Core and UI
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Audio
    implementation("androidx.media:media:1.6.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-work:1.1.0")
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    // Serialization & DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // Networking
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Image Loading & Lottie
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Other UI components
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}

