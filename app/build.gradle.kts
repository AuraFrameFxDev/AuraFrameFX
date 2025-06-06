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
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 31
        targetSdk = 36
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
        }
        create("vanilla") {
            dimension = "xposed"
        }
    }
    
    // Skip xposed flavor to avoid LSPosed dependency issues using the new API
    androidComponents {
        beforeVariants { variantBuilder ->
            if (variantBuilder.flavorName?.contains("xposed", ignoreCase = true) == true) {
                variantBuilder.enable = false
            }
        }
    }

    // Repositories are now defined in settings.gradle.kts instead of here
    // This prevents the 'Build was configured to prefer settings repositories' error

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
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

    kotlin {
        sourceSets.all { // Or sourceSets.getByName("main") { ... } etc.
            languageSettings {
                optIn("kotlinx.serialization.InternalSerializationApi") // Replace with your actual API
                optIn("kotlin.RequiresOptIn")
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
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
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

    "xposedCompileOnly"(files())
    "xposedCompileOnly"("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    "xposedCompileOnly"("io.github.libxposed:api:100-1.0.0")
    "xposedCompileOnly"("io.github.libxposed:service:100-1.0.0")
}

tasks.register(
    "generateOpenApiCode",
    org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class
) {
    inputSpec.set(layout.projectDirectory.file("src/main/resources/auraframefx_ai_api.yaml").asFile.absolutePath)
    generatorName.set("kotlin")
    outputDir.set(layout.buildDirectory.dir("generated").get().asFile.absolutePath)
    apiPackage.set("dev.aurakai.auraframefx.generated.api.auraframefxai")
    modelPackage.set("dev.aurakai.auraframefx.generated.model.auraframefxai")
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
}}