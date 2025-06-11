plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    // Firebase plugins - comment out if google-services.json is not available
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.openapi.generator")
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    lint {
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

    kotlinOptions {
        jvmTarget = "17"
        languageVersion = "1.9"
        apiVersion = "1.9"
        freeCompilerArgs = listOf(
            "-Xjvm-default=all",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-opt-in=kotlin.time.ExperimentalTime",
            "-opt-in=kotlin.experimental.ExperimentalTypeInference",
            "-opt-in=kotlin.ExperimentalStdlibApi",
            "-opt-in=kotlin.concurrent.ExperimentalAtomicApi",
            "-opt-in=kotlin.experimental.ExperimentalNativeApi"
            // Removed -Xcontext-receivers as it's not compatible with Kotlin 1.9.22
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion =
            rootProject.extra["composeCompilerVersion"] as String // Using 1.5.14 from root project
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

    hilt {
        enableAggregatingTask = true
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.ERROR)
}

dependencies {
    // Core Android
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2") // Downgraded to be compatible with AGP 8.2.2
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") // Downgraded
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2") // Downgraded
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2") // Downgraded
    implementation("androidx.activity:activity-compose:1.8.2")

    // Kotlin Serialization - using versions forced by root build.gradle.kts
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
    // XML serialization commented out as it's not available in this version
    // implementation("org.jetbrains.kotlinx:kotlinx-serialization-xml:0.70.0")

    // Dagger Hilt - using version from root build.gradle.kts
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    ksp("com.google.dagger:hilt-compiler:${rootProject.extra["hiltVersion"]}")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // Version from version catalog
    implementation("androidx.hilt:hilt-work:1.1.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2025.06.00")) // Updated to latest BOM from version catalog
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.5") // Downgraded to be compatible with AGP 8.2.2
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Work Manager
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.hilt:hilt-work:1.1.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // UI Components
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // Compose Glance
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance:1.0.0")

    // Vertex AI
    // implementation("com.google.cloud:vertexai:0.3.0") // Removed temporarily due to resolution issue
    implementation("com.google.cloud:google-cloud-aiplatform:3.50.0") {
        // Exclude conflicting classes that are also in Firebase dependencies
        exclude(group = "com.google.api.grpc", module = "proto-google-common-protos")
    }

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-crashlytics")

    // Xposed - using only Maven repository dependencies
    implementation("de.robv.android.xposed:api:82")
    implementation("de.robv.android.xposed:api:82:sources")

    // Define a custom configuration for Xposed dependencies that might be missing
    configurations {
        create("xposedCompileOnly") {
            description = "Configuration for Xposed compile-only dependencies"
            isCanBeConsumed = false
            isCanBeResolved = false
        }
    }

    // Local JAR files commented out as they're missing
    // compileOnly(files("libs/xposed-bridge.jar"))
    // compileOnly(files("libs/xposed-art.jar"))

    // Using LSPosed HiddenApiBypass instead of the XanderYe version
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")

    // LibXposed - commented out due to resolution issues
    // implementation("io.github.libxposed:api:100-preview.5")
    // implementation("io.github.libxposed:service:100-preview.5")

    // Timber and Network
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Reverted to widely available version
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Matched with retrofit version
    // Using the correctly named converter for kotlinx serialization
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("io.mockk:mockk:1.13.8")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
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
