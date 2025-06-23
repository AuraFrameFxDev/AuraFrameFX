// app/build.gradle.kts - ADVANCED VERSION

import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    // Version catalog aliases
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.openapi.generator)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 34

    ndkVersion = "29.0.13113456"
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += "arm64-v8a"
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

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    // Include OpenAPI-generated sources
    sourceSets {
        getByName("main").java.srcDir("${layout.buildDirectory.get()}/generated/openapi/src/main/kotlin")
    }
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // Local JAR for Xposed API
    compileOnly(files("libs/xposed-api-82.jar"))
}

tasks.withType<GenerateTask> {
    generatorName.set("kotlin")
    inputSpec.set("$projectDir/src/main/resources/api/genesis-api.yaml")
    outputDir.set("${layout.buildDirectory.get()}/generated/openapi")
    apiPackage.set("com.auraframes.fx.genesis.api.client")
    modelPackage.set("com.auraframes.fx.genesis.api.model")
    configOptions.set(
        mapOf(
            "library" to "jvm-retrofit2",
            "serializationLibrary" to "kotlinx-serialization",
            "dateLibrary" to "java8",
            "useCoroutines" to "true",
            "enumPropertyNaming" to "UPPERCASE"
        )
    )
}

// Ensure OpenAPI code generation runs before Kotlin compilation
androidComponents {
    onVariants { variant ->
        tasks.named("compile${variant.name.replaceFirstChar { it.uppercase() }}Kotlin") {
            dependsOn(tasks.named("openApiGenerate"))
        }
    }
}
