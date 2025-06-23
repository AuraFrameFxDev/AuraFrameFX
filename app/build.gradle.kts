plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin") // Apply by ID
    id("org.jetbrains.compose")

}

// Common versions
val composeVersion = "1.5.4"
val firebaseBomVersion = "32.7.0"
val lifecycleVersion = "2.6.2"

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
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

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            "-Xjvm-default=all"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
        aidl = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompilerVer.get()

    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "36.0.3"
}

// OpenAPI Generator configuration
val generatedSourcesDir = layout.buildDirectory.dir("generated")

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("generateOpenApi") {
    generatorName.set("kotlin")
    inputSpec.set("${project.projectDir}/src/main/resources/auraframefx_ai_api.yaml")
    outputDir.set(generatedSourcesDir.get().asFile.absolutePath)
    apiPackage.set("dev.aurakai.auraframefx.api")
    modelPackage.set("dev.aurakai.auraframefx.model")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "serializationLibrary" to "kotlinx_serialization",
            "useCoroutines" to "true",
            "enumPropertyNaming" to "UPPERCASE",
            "serializableModel" to "true"
        )
    )
    ignoreFileOverride.set("${project.projectDir}/.openapi-generator-ignore")
}

kotlin {
    jvmToolchain(21)

}

// Ensure the OpenAPI generation happens before compilation
tasks.named("preBuild") {
    dependsOn("generateOpenApi")
}

// Xposed framework configurations
val xposedApiJar = files("${project.rootDir}/libs/api-82.jar")
val xposedBridgeJar = files("${project.rootDir}/libs/bridge-82.jar")
val xposedCompileOnly = configurations.create("xposedCompileOnly")

dependencies {

    // Core Android
    coreLibraryDesugaring(libs.desugarJdkLibs)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.xmlutil.serialization) // Changed to community XML library


    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.hilt:hilt-work:1.1.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:$composeVersion")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // Firebase AI (Gemini)
    implementation(libs.firebase.ai)
    implementation(libs.guava.android) // Recommended for Firebase AI
    implementation(libs.reactive.streams) // Required by Firebase AI

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.junit)

    // Google Cloud
    implementation(platform("com.google.cloud:libraries-bom:26.25.0"))
    implementation("com.google.cloud:google-cloud-generativeai")


    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Work Manager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // UI Components
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // Compose Glance
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-compose:1.0.0")

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.crashlytics)

    // Google Cloud AI - using BOM for version management
    // implementation(platform(libs.google.cloud.bom)) // Duplicate removed
    // implementation("com.google.cloud:google-cloud-generativeai") // Commented out
    // implementation(libs.google.cloud.generativeai) // Removed

    // Timber
    implementation(libs.timber)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.converter.kotlinx.serialization)


    // Xposed dependencies - using local JARs
    compileOnly(xposedApiJar)
    compileOnly(xposedBridgeJar)

    // Xposed hidden API bypass
    xposedCompileOnly(libs.xposed.hiddenapibypass)

    // For development and documentation
    compileOnly(xposedApiSourcesJar) // Only needed for development
    compileOnly(xposedBridgeSourcesJar) // Only needed for development

    // LSPosed API (if using LSPosed specific features)
    // xposedCompileOnly("org.lsposed:libxposed:82") // Commented out to isolate build issues
    // xposedCompileOnly("org.lsposed:libxposed:82:sources") // Commented out to isolate build issues

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("com.google.truth:truth:1.1.5")

    // AndroidX Test
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // Debug implementations
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Desugar JDK libs for Java 8+ APIs on older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}
