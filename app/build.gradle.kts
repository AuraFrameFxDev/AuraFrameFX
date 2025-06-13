// app/build.gradle.kts

@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage", "DEPRECATION")

import org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode

// Xposed JAR files configuration
val xposedApiJar = files("libs/api-82.jar")
val xposedBridgeJar = files("libs/bridge-82.jar")
// xposedApiSourcesJar and xposedBridgeSourcesJar are not compilation dependencies and removed.
// If needed for IDE source attachment, ensure the .jar and -sources.jar are simply in the 'libs' folder.

plugins {
    // Apply plugins using alias() from libs.versions.toml
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.compose) # org.jetbrains.compose plugin (for Multiplatform Compose)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt) # Keep Kapt if needed for Hilt until fully KSP migrated
    alias(libs.plugins.parcelize)
    alias(libs.plugins.kotlin.compose.compiler) # Kotlin Compose Compiler plugin
    // Apply Firebase Crashlytics and Performance plugins here if the app module directly uses them.
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36 # Android API level for compilation

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    // Modern Kotlin compilation options - placed inside 'android' block
    kotlin {
        jvmToolchain(21) // Sets JVM toolchain to Java 21
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
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

    composeOptions {
        // Kotlin Compiler Extension Version should align with your Kotlin version,
        // or a specific version compatible with your Compose BOM.
        kotlinCompilerExtensionVersion = libs.versions.kgp.get()
    }


    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Xposed configuration
        buildConfigField("String", "XPOSED_API_VERSION", "\"82\"")
        buildConfigField("String", "LSPOSED_PACKAGE_NAME", "\"de.robv.android.xposed\"")
        buildConfigField("int", "LSPOSED_API_VERSION", "82")

        // Room schema location for annotation processing
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true",
                    "dagger.fastInit" to "enabled"
                )
            }
        }
    }

    signingConfigs {
        create("release") {
            // IMPORTANT: Manage these securely outside version control (e.g., gradle.properties, environment variables)
            storeFile = file(System.getenv("AURAFRAME_KEYSTORE") ?: "your_keystore_file.jks")
            storePassword = System.getenv("AURAFRAME_KEYSTORE_PASSWORD") ?: "your_keystore_password"
            keyAlias = System.getenv("AURAFRAME_KEY_ALIAS") ?: "your_key_alias"
            keyPassword = System.getenv("AURAFRAME_KEY_PASSWORD") ?: "your_key_password"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    lint {
        checkDependencies = true
        lintConfig = file("lint.xml")
        ignoreTestSources = true
        abortOnError = false
        warningsAsErrors = true
        checkReleaseBuilds = false
        checkAllWarnings = true

        disable.addAll(
            listOf(
                "MissingTranslation",
                "VectorPath",
                "MissingIf"
            )
        )
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("META-INF/licenses/**")
            excludes.add("META-INF/LICENSE*")
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDir("build/generated/src/main/kotlin")
        }
    }

    buildToolsVersion = "36.0.0"
}

// Kotlin JVM compile configuration for the module (should be outside 'android' block)
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(JvmTargetValidationMode.ERROR)
}

// Xposed framework configuration - must be compileOnly as it's provided by the Xposed framework at runtime
val xposedCompileOnly = configurations.create("xposedCompileOnly")

dependencies {
    // Core Android
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Protocol Buffers and Netty (from libs.versions.toml)
    implementation(libs.google.protobuf.java)
    implementation(libs.commons.io)
    implementation(libs.netty.codec.http2)
    implementation(libs.netty.handler)
    implementation(libs.bouncycastle.bcprov)
    implementation(libs.netty.common)
    implementation(libs.apache.commons.compress)
    implementation(libs.guava)
    implementation(libs.netty.codec.http)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.xml)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)

    // Kotlin Coroutines (using bundle from libs.versions.toml)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Accompanist (from libs.versions.toml)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)
    // Add other Accompanist libs if needed and defined in libs.versions.toml
    // implementation(libs.accompanist.navigation.animation)
    // implementation(libs.accompanist.pager)
    // implementation(libs.accompanist.pager.indicators)

    // Compose UI (using BOM and bundle from libs.versions.toml)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose) # Includes androidx-compose-ui, material3, preview, foundation, runtime
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Google Cloud AI (from libs.versions.toml)
    implementation(platform(libs.google.cloud.bom))
    implementation(libs.google.cloud.generativeai)
    implementation(libs.google.generative.ai) # Changed from google.ai.generative.ai for simplified alias

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.junitJupiter.api)
    testRuntimeOnly(libs.junitJupiter.engine)
    testImplementation(libs.junitJupiter.params)

    // Room (from libs.versions.toml)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Work Manager (from libs.versions.toml)
    implementation(libs.androidx.work.runtime.ktx)

    // DataStore (from libs.versions.toml)
    implementation(libs.androidx.datastore.preferences)

    // UI Components
    implementation(libs.androidx.cardview)
    implementation(libs.coil.compose)

    // Compose Glance (from libs.versions.toml)
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3) # Corrected name from glance.compose

    // Firebase (using BOM and individual libraries from libs.versions.toml)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx) # Corrected alias from firebase.storage
    implementation(libs.firebase.crashlytics.ktx) # Corrected alias from firebase.crashlytics.lib
    implementation(libs.firebase.perf.ktx) # If you need performance monitoring

    // Timber (from libs.versions.toml)
    implementation(libs.timber)

    // Network (from libs.versions.toml)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    // Xposed dependencies - using local JARs and remote (from libs.versions.toml where possible)
    compileOnly(xposedApiJar)
    compileOnly(xposedBridgeJar)
    xposedCompileOnly(libs.xposed.hiddenapibypass)
    xposedCompileOnly(libs.lsposed.libxposed)
    // For sources, use the direct string here as 'classifier' is not supported as a key in libs.versions.toml library def
    xposedCompileOnly("org.lsposed:libxposed:82:sources")

    // Testing (from libs.versions.toml)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.espresso.core)

    // ML Kit (from libs.versions.toml)
    // implementation(libs.mlkit.barcode.scanning)
    // implementation(libs.mlkit.text.recognition)

    // CameraX (from libs.versions.toml)
    // implementation(libs.androidx.camera.core)
    // implementation(libs.androidx.camera.camera2)
    // implementation(libs.androidx.camera.lifecycle)
    // implementation(libs.androidx.camera.view)

    // TensorFlow Lite (from libs.versions.toml)
    // implementation(libs.tensorflow.lite)
    // implementation(libs.tensorflow.lite.support)
    // implementation(libs.tensorflow.lite.metadata)
    // implementation(libs.tensorflow.lite.gpu)
    // implementation(libs.tensorflow.lite.task.vision)
    // implementation(libs.tensorflow.lite.task.audio)
    // implementation(libs.tensorflow.lite.task.text)

    // gRPC (from libs.versions.toml)
    // implementation(platform(libs.grpc.bom))
    // implementation(libs.grpc.okhttp)
    // implementation(libs.grpc.protobuf)
    // implementation(libs.grpc.stub)
    // implementation(libs.protobuf.kotlin) # Use protobuf-kotlin if you need Kotlin Protobuf
    // implementation(libs.javax.annotation.api)

    // AndroidX Media (from libs.versions.toml)
    // implementation(libs.androidx.media)
}

// OpenAPI Generator configuration - must be in the application module's build.gradle.kts
openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$projectDir/src/main/resources/auraframefx_ai_api.yaml")
    outputDir.set("$buildDir/generated/openapi")
    apiPackage.set("dev.aurakai.auraframefx.generated.api.auraframefxai")
    modelPackage.set("dev.aurakai.auraframefx.generated.model.auraframefxai")
    modelNameSuffix.set("Dto")
    generateModelDocumentation.set(true)
    generateApiDocumentation.set(true)
    
    // Configure additional properties
    configOptions.set(
        mapOf(
            "library" to "jvm-retrofit2",
            "serializationLibrary" to "kotlinx_serialization",
            "useCoroutines" to "true",
            "dateLibrary" to "java8",
            "enumPropertyNaming" to "UPPERCASE",
            "collectionType" to "list",
            "parcelizeModels" to "true",
            "useTags" to "true"
        )
    )
    
    // Add type mappings for better Kotlin support
    typeMappings.set(
        mapOf(
            "DateTime" to "java.time.OffsetDateTime",
            "Date" to "java.time.LocalDate",
            "Time" to "java.time.OffsetTime"
        )
    )
    
    // Add import mappings
    importMappings.set(
        mapOf(
            "java.time.OffsetDateTime" to "java.time.OffsetDateTime",
            "java.time.LocalDate" to "java.time.LocalDate",
            "java.time.OffsetTime" to "java.time.OffsetTime"
        )
    )
}

tasks.register("validateOpenApiSpec") {
    val specFile = file("$projectDir/src/main/resources/auraframefx_ai_api.yaml")
    doLast {
        if (!specFile.exists()) {
            logger.warn("API spec file not found at: ${specFile.absolutePath}")
            logger.warn("OpenAPI code generation may fail or use stale code.")
        } else {
            logger.lifecycle("OpenAPI spec file found at: ${specFile.absolutePath}")
        }
    }
}

tasks.named("openApiGenerate") {
    dependsOn("validateOpenApiSpec")
    mustRunAfter(tasks.named("clean"))
}

tasks.register<Delete>("cleanOpenApiGenerated") {
    delete(layout.buildDirectory.dir("generated/openapi"))
}

tasks.named("clean") {
    finalizedBy("cleanOpenApiGenerated")
}

// Ensure generated sources are included in the main source set
sourceSets.main {
    java.srcDirs("$buildDir/generated/openapi/src/main/kotlin")
}

// Make sure the OpenAPI generated code is available before compilation
tasks.named("compileKotlin") {
    dependsOn("openApiGenerate")
}

tasks.named("preBuild") {
    dependsOn("openApiGenerate")
}