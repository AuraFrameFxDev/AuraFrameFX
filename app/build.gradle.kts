// app/build.gradle.kts - CLEANED & CORRECTED

import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    // Android application plugin via version catalog
    alias(libs.plugins.android.application)
    // Kotlin Android plugin via version catalog
    alias(libs.plugins.kotlin.android)
    // Kotlin Serialization plugin via version catalog
    alias(libs.plugins.kotlin.serialization)
    // Uncomment these as needed:
    // alias(libs.plugins.ksp)
    // alias(libs.plugins.hilt)
    // alias(libs.plugins.openapi.generator)
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

    // If you enable OpenAPI generator, uncomment the following sourceSets line
    // sourceSets {
    //     getByName("main").java.srcDir("${layout.buildDirectory.get()}/generated/openapi/src/main/kotlin")
    // }
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    // Uncomment for Hilt support:
    // implementation(libs.hilt.android)
    // Uncomment for KSP support:
    // ksp(libs.hilt.compiler)
    // Local JAR for Xposed API
    compileOnly(files("libs/xposed-api-82.jar"))
}

// Uncomment and configure for OpenAPI code generation
/*
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
*/

// Uncomment when enabling OpenAPI Generator
/*
androidComponents {
    onVariants { variant ->
        tasks.named("compile${variant.name.replaceFirstChar { it.uppercase() }}Kotlin") {
            dependsOn(tasks.named("openApiGenerate"))
        }
    }
}
*/
