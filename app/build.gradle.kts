@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage", "DEPRECATION")

import org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode

// Xposed JAR files configuration
val xposedApiJar = files("libs/api-82.jar")
val xposedBridgeJar = files("libs/bridge-82.jar")
// REMOVED: xposedApiSourcesJar and xposedBridgeSourcesJar. These are source files, not for compilation.

plugins {
    // ONLY use alias() for plugins defined in libs.versions.toml
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt) # Corrected Hilt plugin alias
    alias(libs.plugins.google.services)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.compose) # org.jetbrains.compose plugin
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt) # Kapt is still used for Hilt, KSP for Room.
    alias(libs.plugins.parcelize)
    alias(libs.plugins.kotlin-compose-compiler) # Corrected alias for Compose Compiler
    // If your app module actually uses Firebase Crashlytics directly, apply it here
    // alias(libs.plugins.firebaseCrashlytics)
    // Add any other actual Gradle plugins (e.g., Jib) if defined in libs.versions.toml
    // alias(libs.plugins.google.cloud.tools.jib)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    // Modern Kotlin compilation options (replaces old kotlinOptions {})
    kotlin {
        jvmToolchain(21)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
            freeCompilerArgs.addAll(
                "-Xjvm-default=all",
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=kotlin.time.ExperimentalTime",
                "-opt-in=kotlin.experimental.ExperimentalTypeInference",
                "-opt-in=kotlin.ExperimentalStdlibApi",
                "-opt-in=kotlin.concurrent.ExperimentalAtomicApi",
                "-opt-in=kotlin.experimental.ExperimentalNativeApi",
                "-Xcontext-receivers"
            )
        }
    }

    composeOptions {
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
            // Ensure these are securely managed (e.g., gradle.properties, environment variables)
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
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(JvmTargetValidationMode.ERROR)
}

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

    // Protocol Buffers and Netty
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

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Permissions
    implementation(libs.accompanist.permissions)

    // Compose (Using BOM and bundle)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Google Cloud AI
    implementation(platform(libs.google.cloud.bom))
    implementation(libs.google.cloud.generativeai)
    implementation(libs.google.ai.generative.ai)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.junitJupiter.api)
    testRuntimeOnly(libs.junitJupiter.engine)
    testImplementation(libs.junitJupiter.params)

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
    implementation(libs.firebase.crashlytics.lib)

    // Timber
    implementation(libs.timber)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    // Xposed dependencies - using local JARs and remote
    compileOnly(xposedApiJar)
    compileOnly(xposedBridgeJar)
    xposedCompileOnly(libs.xposed.hiddenapibypass)
    xposedCompileOnly(libs.lsposed.libxposed)
    xposedCompileOnly(libs.lsposed.libxposed.sources)

    // Testing
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.espresso.core)
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$projectDir/src/main/resources/auraframefx_ai_api.yaml")
    outputDir.set(layout.buildDirectory.dir("generated/openapi").map { it.asFile.absolutePath })
    apiPackage.set("dev.aurakai.auraframefx.generated.api.auraframefxai")
    modelPackage.set("dev.aurakai.auraframefx.generated.model.auraframefxai")
    configOptions.set(
        mapOf(
            "library" to "jvm-retrofit2",
            "serializationLibrary" to "kotlinx_serialization",
            "useCoroutines" to "true",
            "dateLibrary" to "java8",
            "enumPropertyNaming" to "UPPERCASE"
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

tasks.named("preBuild") {
    dependsOn("openApiGenerate")
}