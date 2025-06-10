plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.openapi.generator")
}

android {
    namespace = "com.example.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 26
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
        debug {
            isMinifyEnabled = false
        }
    }

    flavorDimensions += "xposed"

    productFlavors {
        create("xposed") {
            dimension = "xposed"
            // Skip this flavor during CI builds to avoid LSPosed dependency issues
            // if (System.getenv("CI") == "true") {
            //     setIgnore(true)
            // }
        }
        create("vanilla") {
            dimension = "xposed"
        }
    }

    androidComponents {
        beforeVariants { variantBuilder ->
            if (variantBuilder.name == "xposedDebug" || variantBuilder.name == "xposedRelease") {
                if (System.getenv("CI") == "true") {
                    variantBuilder.enable = false
                }
            }
        }
    }

    // Repositories are now defined in settings.gradle.kts instead of here
    // This prevents the 'Build was configured to prefer settings repositories' error

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Add compiler flags for annotation processing
        freeCompilerArgs += listOf(
            "-Xskip-prerelease-check",
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    
    // Configure kapt for Hilt
    kapt {
        correctErrorTypes = true
        useBuildCache = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    android.sourceSets {
        getByName("main") {
            java.srcDirs("build/generated/src/main/kotlin")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    // Removed appcompat since we're using pure Compose

    // Jetpack Navigation with built-in animation support
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.dagger:hilt-android:2.50")
    // kapt("com.google.dagger:hilt-compiler:2.50")
    // Temporarily disabled KSP for Hilt due to compatibility issues
    ksp("com.google.dagger:hilt-compiler:2.50")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.ai.client.generativeai:generativeai:0.3.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.02"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("io.mockk:mockk-agent:1.13.10")
    testImplementation("io.mockk:mockk-android:1.13.10")
    androidTestImplementation("io.mockk:mockk-android:1.13.10")

    // Only include LSPosed dependencies if not in CI environment
    if (System.getenv("CI") != "true") {
        "xposedCompileOnly"("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
        // For local development, still try to use the actual dependencies
        "xposedCompileOnly"("io.github.libxposed:api:100-1.0.0")
        "xposedCompileOnly"("io.github.libxposed:service:100-1.0.0")
    } else {
        // In CI, use empty file lists to avoid dependency resolution failures
        "xposedCompileOnly"(files())
    }
}

// Create a specific task for generating the OpenAPI code
tasks.register(
    "generateOpenApiCode",
    org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class
) {
    inputSpec.set(layout.projectDirectory.file("src/main/resources/auraframefx_ai_api.yaml").asFile.absolutePath)
    generatorName.set("kotlin")
    outputDir.set(layout.buildDirectory.dir("generated").get().asFile.absolutePath)
    apiPackage.set("com.example.app.generated.api.auraframefxai")
    modelPackage.set("com.example.app.generated.model.auraframefxai")
    configOptions.set(
        mapOf(
            "library" to "jvm-retrofit2",
            "serializationLibrary" to "kotlinx_serialization",
            "singleModelWithWrapper" to "false",
            "enumClassPrefix" to "true",
            "dateLibrary" to "java8",
            "useCoroutines" to "true"
        )
    )
}
// Repositories are defined in settings.gradle.kts

// Run OpenAPI generation before build
tasks.named("preBuild").configure {
    dependsOn("generateOpenApiCode")
}

// Clean the generated code when running clean task
tasks.named("clean").configure {
    doLast {
        delete(layout.buildDirectory.dir("generated").get())
    }
}