@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage", "DEPRECATION")

import org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode

// Xposed JAR files configuration
val xposedApiJar = files("libs/api-82.jar")
val xposedBridgeJar = files("libs/bridge-82.jar")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.compose)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    buildFeatures {
        compose = true
        buildConfig = true
    }
    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "XPOSED_API_VERSION", "\"82\"")
        buildConfigField("String", "LSPOSED_PACKAGE_NAME", "\"de.robv.android.xposed\"")
        buildConfigField("int", "LSPOSED_API_VERSION", "82")
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
        disable.addAll(listOf("MissingTranslation", "VectorPath", "MissingIf"))
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
    kotlin {
        jvmToolchain(21)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
            freeCompilerArgs.addAll(
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
}

val xposedCompileOnly = configurations.create("xposedCompileOnly")

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.ai.generative.ai)
    implementation(libs.google.material)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.coil.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.compose)
    xposedCompileOnly(xposedApiJar)
    xposedCompileOnly(xposedBridgeJar)
    xposedCompileOnly("org.lsposed.hiddenapibypass:hiddenapibypass:6.1")
    xposedCompileOnly("org.lsposed:libxposed:82")
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    implementation("com.google.protobuf:protobuf-java:4.31.1")
    implementation("commons-io:commons-io:2.19.0")
    implementation("io.netty:netty-codec-http2:4.1.100.Final")
    implementation("io.netty:netty-handler:4.1.118.Final")
    implementation("org.bouncycastle:bcprov-jdk18on:1.81")
    implementation("io.netty:netty-common:4.1.118.Final")
    implementation("org.apache.commons:commons-compress:1.27.1")
    implementation("com.google.guava:guava:33.4.8-android")
    implementation("io.netty:netty-codec-http:4.1.118.Final")
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(JvmTargetValidationMode.ERROR)
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