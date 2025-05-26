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
    
    // Kotlin standard library plugins
    id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin.get()
    id("org.jetbrains.kotlin.plugin.noarg") version libs.versions.kotlin.get()
    id("org.jetbrains.kotlin.plugin.allopen") version libs.versions.kotlin.get()
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        named("main") {
            java.srcDirs("src/main/java", "src/main/kotlin")
            res.srcDirs("src/main/res")
            assets.srcDirs("src/main/assets")
            resources.srcDirs("src/main/resources")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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

        // Local properties are loaded at the top level

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
    implementation(platform(libs.kotlinStdlib))
    implementation(libs.kotlinStdlib)
    implementation(libs.kotlinStdlibJdk8)
    implementation(libs.kotlinReflect)
    
    // Explicit Kotlin standard library dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${libs.versions.kotlin.get()}")

    // Google Cloud Vertex AI
    implementation("com.google.cloud:google-cloud-vertexai:1.23.0")
    implementation("com.google.cloud:google-cloud-aiplatform:3.34.0")
    implementation("com.google.cloud:google-cloud-storage:2.28.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.35.0")
    implementation("com.google.api-client:google-api-client:2.8.0")
    implementation("com.google.apis:google-api-services-aiplatform:v1-rev20230913-2.0.0")

    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.performance.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.dynamic.links.ktx)

    // Google Generative AI
    implementation("com.google.generativeai:generative-ai:0.4.0")

    // Google Cloud Vertex AI
    implementation(platform("com.google.cloud:libraries-bom:26.61.0"))
    implementation("com.google.cloud:google-cloud-vertexai")
    implementation("com.google.cloud:google-cloud-aiplatform")
    implementation("com.google.api.grpc:proto-google-cloud-aiplatform-v1")
    implementation("com.google.cloud:google-cloud-core:2.56.0")
    implementation("com.google.api:gax:2.66.0")
    implementation("com.google.api:gax-grpc:2.66.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Accompanist for additional Compose utilities
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.36.0")
    implementation("com.google.accompanist:accompanist-pager:0.36.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.36.0")

    // Other utilities
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Google Play Services - ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("com.google.mlkit:text-recognition:17.0.2")
    implementation("com.google.mlkit:object-detection:17.0.2")
    implementation("com.google.mlkit:image-labeling:17.0.9")
    implementation("com.google.mlkit:face-detection:16.3.0")
    implementation("com.google.mlkit:segmentation-selfie:17.0.0")
    implementation("com.google.mlkit:language-id:17.0.6")
    implementation("com.google.mlkit:entity-extraction:16.0.0")
    implementation("com.google.mlkit:smart-reply:17.0.4")
    implementation("com.google.mlkit:translate:17.0.3")
    implementation("com.google.mlkit:language:17.0.4")

    // CameraX
    val camerax_version = "1.4.2"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")

    // TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:2.17.0")

    // Dagger Hilt for dependency injection
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.56.2")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.5.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.5.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.17.0")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")

    // Google Cloud Core
    implementation("com.google.cloud:google-cloud-core:2.56.0")
    implementation("com.google.api:gax:2.66.0")
    implementation("com.google.api:gax-grpc:2.66.0")
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    // Kotlin standard library
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-common"))

    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Javax Inject for dependency injection
    implementation("javax.inject:javax.inject:1")

    // Dagger for dependency injection
    implementation("com.google.dagger:dagger:2.56.2")
    ksp("com.google.dagger:dagger-compiler:2.56.2")

    // Google Auth and API Client
    implementation("com.google.auth:google-auth-library-oauth2-http:1.35.0")
    implementation("com.google.auth:google-auth-library-credentials:1.35.0")
    implementation("com.google.api-client:google-api-client:2.8.0")

    // gRPC and Protobuf
    implementation(platform("io.grpc:grpc-bom:1.72.0"))
    implementation("io.grpc:grpc-okhttp")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("com.google.protobuf:protobuf-java:4.31.0")
    implementation("com.google.protobuf:protobuf-kotlin:4.31.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    // TensorFlow Lite for on-device ML
    implementation("org.tensorflow:tensorflow-lite:2.17.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.5.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.5.0")
    implementation("org.tensorflow:tensorflow-lite-task-audio:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-text:0.4.4")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.0")

    // AndroidX Core and UI
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Audio
    implementation("androidx.media:media:1.7.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.56.2")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // Serialization & DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Other UI components
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
}

