@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage", "DEPRECATION")

// Xposed JAR files configuration
val xposedApiJar = files("libs/api-82.jar")
val xposedBridgeJar = files("libs/bridge-82.jar")
val xposedApiSourcesJar = files("libs/api-82-sources.jar")
val xposedBridgeSourcesJar = files("libs/bridge-82-sources.jar")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.compose")
    id("org.openapi.generator")
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

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
            isDebuggable = true
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
    }

    kotlinOptions {
        jvmTarget = "21"
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
            "-opt-in=kotlin.experimental.ExperimentalNativeApi",
            "-Xcontext-receivers"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
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
    jvmToolchain(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.ERROR)
}

// Xposed framework configuration - must be compileOnly as it's provided by the Xposed framework at runtime
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
    implementation("io.github.pdvrieze.xmlutil:core-jvm:0.86.3")
    implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.91.1")
    implementation(libs.kotlinx.serialization.xml) // Now uses 0.70.0

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Permissions (use Accompanist)
    implementation(libs.accompanist.permissions)
    // Removed: implementation(libs.androidx.permission)
    // Removed: implementation(libs.androidx.permission.runtime)
    // Removed: implementation(libs.androidx.permission.group)

    // Compose BOM - Using BOM to manage all Compose versions
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.lottie.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // UI Components
    implementation(libs.androidx.cardview)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)

    // Compose Glance
    implementation(libs.glance.appwidget)
    implementation(libs.glance.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.crashlytics)

    // Google Cloud AI - using BOM for version management
    implementation(platform(libs.google.cloud.bom))
    implementation("com.google.cloud:google-cloud-generativeai")
    implementation(libs.google.cloud.generativeai) // Now uses 0.8.0

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
    xposedCompileOnly("org.lsposed:libxposed:82")
    xposedCompileOnly("org.lsposed:libxposed:82:sources") // For development only

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
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