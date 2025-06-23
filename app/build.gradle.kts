// app/build.gradle.kts - CORRECTED AND FINAL

import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    // Apply the Android application plugin
    alias(libs.plugins.android.application)
    
    // Apply the Kotlin Android plugin
    alias(libs.plugins.kotlin.android)
    
    // Apply other plugins
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.openapi.generator)
}

// The 'android' block starts here...
android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 35
    ndkVersion = "29.0.13113456"
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx" // Let's make this match the namespace for consistency
        minSdk = 34
        targetSdk = 35
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

    sourceSets {
        getByName("main").java.srcDir("${layout.buildDirectory.get()}/generated/openapi/src/main/kotlin")
    }
} // <-- ...and the 'android' block correctly ends HERE.

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Using your local JAR file for the Xposed API
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
androidComponents {
    onVariants { variant ->
        tasks.named("compile${variant.name.replaceFirstChar { it.uppercase() }}Kotlin") {
            dependsOn(tasks.named("openApiGenerate"))
        }
    }
}// --- ADD THIS BLOCK AT THE END ---
// This is the correct, modern way to hook into the Android build process.
// It waits until the variants are created and then adds the dependency.
androidComponents {
    onVariants { variant ->
        tasks.named("compile${variant.name.replaceFirstChar { it.uppercase() }}Kotlin") {
            dependsOn(tasks.named("openApiGenerate"))
        }
    }
}