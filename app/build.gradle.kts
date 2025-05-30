import java.io.FileInputStream
import java.util.Properties

// Load local.properties file if it exists
val localProperties = Properties().apply {
    try {
        val localFile = rootProject.file("local.properties")
        if (localFile.exists()) {
            FileInputStream(localFile).use { load(it) }
        }
    } catch (e: Exception) {
        logger.warn("Could not load local.properties: ${e.message}")
    }
}

// Apply core plugins
plugins {
    // Core Android and Kotlin plugins
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    
    // Firebase plugins for analytics and crash reporting
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.performance)
    
    // Dependency injection and code generation
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    
    // Serialization support
    alias(libs.plugins.kotlin.serialization)
    
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = libs.versions.compileSdk.get().toInt() // Use version catalog

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = libs.versions.minSdk.get().toInt() // Use version catalog, taking 26
        targetSdk = libs.versions.targetSdk.get().toInt() // Use version catalog
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Enable BuildConfig generation
        buildConfigField("boolean", "DEBUG_MODE", "true")

        // Google Cloud configuration from local.properties
        val googleCloudProjectId = localProperties.getProperty("GOOGLE_CLOUD_PROJECT_ID", "")
        val googleCloudApiKey = localProperties.getProperty("GOOGLE_CLOUD_API_KEY", "")

        buildConfigField("String", "GOOGLE_CLOUD_PROJECT_ID", "\"$googleCloudProjectId\"")
        buildConfigField("String", "GOOGLE_CLOUD_API_KEY", "\"$googleCloudApiKey\"")

        // Add Google Cloud API key to manifest for network security config
        manifestPlaceholders["googleCloudApiKey"] = googleCloudApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = true // More common for release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug { // It's good practice to have a debug type explicitly
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all" // Keep this if it was intentionally added
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true // Keeping this as not explicitly asked to remove
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    sourceSets {
        named("main") {
            java.srcDirs("src/main/java", "src/main/kotlin")
            res.srcDirs("src/main/res")
            assets.srcDirs("src/main/assets", "app/secure") // Consolidated assets
            resources.srcDirs("src/main/resources")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }
}

dependencies {
    // Kotlin Standard Library
    implementation(libs.kotlin.stdlib) // Adjusted based on common TOML naming
    implementation(libs.kotlin.stdlib.jdk8) // Adjusted
    implementation(libs.kotlin.reflect) // Adjusted

    // Google Cloud Vertex AI & AI Platform (using BOM where possible)
    implementation(platform(libs.google.cloud.libraries.bom))
    implementation("com.google.cloud:google-cloud-vertexai")
    implementation("com.google.cloud:google-cloud-aiplatform")
    implementation("com.google.cloud:google-cloud-storage") // Explicit version was 2.28.1
    implementation("com.google.auth:google-auth-library-oauth2-http") // Explicit version was 1.35.0
    implementation("com.google.api-client:google-api-client") // Explicit version was 2.8.0
    implementation("com.google.apis:google-api-services-aiplatform") // Explicit version was v1-rev20250519-2.0.0

    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material) // Use alias from TOML
    implementation(libs.androidx.constraintlayout) // Use alias from TOML

    // Compose
    implementation(platform(libs.androidx.compose.bom)) // Use alias from TOML
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended") // Keep as it's not in BOM typically
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // Kept explicit 1.2.0

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose) // Already an alias in TOML
    implementation(libs.androidx.activity.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler) // Use the Hilt Android compiler alias
    // kapt(libs.androidx.hilt.compiler) // Removed, as hilt.android.compiler should cover it.

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.performance.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.dynamic.links.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)

    // Google Generative AI
    implementation("com.google.generativeai:generative-ai:0.4.0") // Keep explicit or add to TOML

    // More Google Cloud (ensure these are covered by BOM or add to TOML if specific versions needed)
    // implementation("com.google.api.grpc:proto-google-cloud-aiplatform-v1") // Covered by aiplatform?
    // implementation("com.google.cloud:google-cloud-core") // Covered by BOM
    // implementation("com.google.api:gax") // Covered by BOM
    // implementation("com.google.api:gax-grpc") // Covered by BOM

    // Coroutines (consolidated)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    // Accompanist
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.36.0")
    implementation("com.google.accompanist:accompanist-pager:0.36.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.36.0")

    // Other utilities
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    // implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0") // If needed
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")


    // Google Play Services - ML Kit (keep explicit versions or create a BOM/version group in TOML)
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.google.mlkit:text-recognition:17.0.2")
    // ... other mlkit dependencies

    // CameraX
    val camerax_version = "1.4.2" // Or move to libs.versions.toml
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")

    // TensorFlow Lite (keep explicit or move to TOML)
    implementation("org.tensorflow:tensorflow-lite:2.17.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.5.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.5.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.17.0") // If used
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-audio:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-text:0.4.4")


    // Hilt Work Manager specific (kept explicit versions)
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")


    // gRPC and Protobuf (use BOM if available or manage versions in TOML)
    implementation(platform("io.grpc:grpc-bom:1.72.0"))
    implementation("io.grpc:grpc-okhttp")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("com.google.protobuf:protobuf-java:4.31.0") // Or com.google.protobuf:protobuf-javalite for Android
    implementation("com.google.protobuf:protobuf-kotlin:4.31.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2") // Often for gRPC

    // AndroidX Media & WorkManager
    implementation("androidx.media:media:1.7.0") // Keep explicit or add to TOML
    implementation(libs.androidx.work.runtime.ktx)

    // Networking (using TOML aliases)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Image Loading & Lottie (using TOML aliases)
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose) // Ensure this alias exists and version is correct

    // Testing (using TOML aliases and BOM)
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.14.2") // Keep or add to TOML
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2") // Keep or add to TOML
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

