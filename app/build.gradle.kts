// AuraFrameFxBeta/app/build.gradle.kts
import java.io.FileInputStream
import java.util.Properties
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

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
    
    // Code quality plugins
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)

    // Firebase plugins for analytics and crash reporting
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf) // Corrected alias to match libs.versions.toml

    // Dependency injection and code generation
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)

    // Serialization support
    alias(libs.plugins.kotlin.serialization)

    // Navigation Safe Args plugin
    alias(libs.plugins.navigation.safe.args)
}

// Configure Detekt for this module
configure<DetektExtension> {
    source = files("src/main/java", "src/main/kotlin")
    config = files("${rootProject.rootDir}/config/detekt/detekt-config.yml")
    buildUponDefaultConfig = true
    autoCorrect = true
    
    reports {
        html.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
    }
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 35
    // Use version catalog (35)

    defaultConfig {
        manifestPlaceholders += mapOf()
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 33
        targetSdk = 35
        // Use version catalog
        // Use version catalog (35)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Enable BuildConfig generation
        buildConfigField("boolean", "DEBUG_MODE", "true")

        // Google Cloud configuration from environment variables
        val googleCloudProjectId = System.getenv("GOOGLE_CLOUD_PROJECT_ID") ?: ""
        val googleCloudApiKey = System.getenv("GOOGLE_CLOUD_API_KEY") ?: ""

        // Fallback to local.properties if environment variables are not set
        if (googleCloudProjectId.isEmpty()) {
            googleCloudProjectId = localProperties.getProperty("GOOGLE_CLOUD_PROJECT_ID", "")
        }
        if (googleCloudApiKey.isEmpty()) {
            googleCloudApiKey = localProperties.getProperty("GOOGLE_CLOUD_API_KEY", "")
        }

        buildConfigField("String", "GOOGLE_CLOUD_PROJECT_ID", "\"$googleCloudProjectId\"")
        buildConfigField("String", "GOOGLE_CLOUD_API_KEY", "\"$googleCloudApiKey\"")

        // Add Google Cloud API key to manifest for network security config
        manifestPlaceholders["googleCloudApiKey"] = googleCloudApiKey

        // Security configuration
        buildConfigField("boolean", "IS_DEBUG", "${variant.buildType.isDebug}")
        buildConfigField("String", "SECURE_BASE_URL", "\"${System.getenv("SECURE_BASE_URL") ?: "https://api.auraframefx.com"}\"")
        buildConfigField("String", "SECURE_API_KEY", "\"${System.getenv("SECURE_API_KEY") ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
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
            assets.srcDirs("src/main/assets", "app/secure")
            resources.srcDirs("src/main/resources")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }
}

// Configure ktlint
ktlint {
    android = true
    ignoreFailures = true
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    jvmTarget = JavaVersion.VERSION_17.toString()
}

dependencies {
    // Kotlin Standard Library
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.reflect)
    
    // Detekt
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)

    // Google Cloud Vertex AI & AI Platform (using BOM where possible)
    implementation(platform(libs.google.cloud.libraries.bom))
    implementation("com.google.cloud:google-cloud-vertexai") # No direct alias, but depends on BOM
    implementation("com.google.cloud:google-cloud-aiplatform") # No direct alias, but depends on BOM
    implementation("com.google.cloud:google-cloud-storage") # No direct alias, but depends on BOM
    implementation("com.google.auth:google-auth-library-oauth2-http") # No direct alias
    implementation("com.google.api-client:google-api-client") # No direct alias
    implementation("com.google.apis:google-api-services-aiplatform") # No direct alias

    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended") // Not in BOM, kept explicit
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest) // Corrected alias

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose) // Used alias

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.perf.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.dynamic.links.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)

    // Google Generative AI
    implementation(libs.google.generative.ai) // Used alias

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Accompanist
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // Other utilities
    implementation(libs.timber)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    // Google Play Services - ML Kit
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.mlkit.text.recognition)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.tensorflow.lite.task.vision)
    implementation(libs.tensorflow.lite.task.audio)
    implementation(libs.tensorflow.lite.task.text)

    // Hilt Work Manager specific
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    // gRPC and Protobuf
    implementation(platform(libs.grpc.bom))
    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.protobuf.java)
    implementation(libs.protobuf.kotlin)
    implementation(libs.javax.annotation.api)

    // AndroidX Media & WorkManager
    implementation(libs.androidx.media) // Used alias (assuming it's androidx-media in TOML)
    implementation(libs.androidx.work.runtime.ktx)

    // Networking (using TOML aliases)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Image Loading & Lottie (using TOML aliases)
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)

    // Testing (using TOML aliases and BOM)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}