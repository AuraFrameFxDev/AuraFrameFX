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
    id("org.jetbrains.compose")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        kotlinOptions {
            jvmTarget = "17"
            // Add compiler flags for annotation processing
            freeCompilerArgs += listOf(
                "-Xskip-prerelease-check",
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                // Add flag to skip metadata version checks
                "-Xskip-metadata-version-check"
            )
            // Add JVM arguments for KAPT compatibility with JDK modules
            kotlinDaemonJvmArgs = listOf(
                "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
            )
        }

        sourceSets.all {
            languageSettings {
                optIn("kotlinx.serialization.InternalSerializationApi")
                optIn("kotlin.RequiresOptIn")
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    // Configure kapt for Hilt
    kapt {
        correctErrorTypes = true
        // Add JVM arguments directly to kapt for JDK 17+ compatibility
        javacOptions {
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED")
            option("--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
        }
        useBuildCache = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }

        // Handle native libraries that can't be stripped
        jniLibs {
            useLegacyPackaging =
                true  // Use legacy packaging for native libraries that can't be stripped

            // Explicitly keep these libraries
            keepDebugSymbols += listOf(
                "**/libandroidx.graphics.path.so",
                "**/libdatastore_shared_counter.so"
            )
        }
    }

    android.sourceSets {
        getByName("main") {
            java.srcDirs("build/generated/src/main/kotlin")
        }
    }

    dependencies {
        implementation("androidx.core:core-ktx:1.16.0")
        implementation("androidx.core:core-splashscreen:1.0.1")
        // Removed appcompat since we're using pure Compose

        // Jetpack Navigation with built-in animation support
        implementation("androidx.navigation:navigation-compose:2.9.0")
        implementation("androidx.navigation:navigation-runtime-ktx:2.9.0")
        implementation("androidx.activity:activity-compose:1.10.1")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
        implementation(platform("androidx.compose:compose-bom:2025.06.00"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-graphics")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.compose.material3:material3")
        implementation("com.google.dagger:hilt-android:2.47")
        kapt("com.google.dagger:hilt-compiler:2.47") // Use kapt instead of ksp for Hilt
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
        implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
        implementation("com.google.firebase:firebase-analytics-ktx")
        implementation("com.google.firebase:firebase-crashlytics-ktx")
        implementation("com.google.firebase:firebase-messaging-ktx")
        implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
        // Retrofit + OkHttp: Networking
        implementation("com.squareup.retrofit2:retrofit:2.9.0") // Downgraded to 2.9.0 for Kotlin 1.9.0 compatibility
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.2.1")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
        androidTestImplementation(platform("androidx.compose:compose-bom:2025.06.00"))
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")
        debugImplementation("androidx.compose.ui:ui-tooling")
        testImplementation("io.mockk:mockk-agent:1.14.2")
        testImplementation("io.mockk:mockk-android:1.14.2")
        androidTestImplementation("io.mockk:mockk-android:1.14.2")

        "xposedCompileOnly"(files())
        "xposedCompileOnly"("org.lsposed.hiddenapibypass:hiddenapibypass:6.1")
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
    }
}
