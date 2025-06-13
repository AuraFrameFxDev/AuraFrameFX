// app/build.gradle.kts

[span_44](start_span)@file:Suppress("UNUSED_VARIABLE", "UnstableApiUsage", "DEPRECATION")[span_44](end_span)

// Xposed JAR files configuration
[span_45](start_span)val xposedApiJar = files("libs/api-82.jar")[span_45](end_span)
[span_46](start_span)val xposedBridgeJar = files("libs/bridge-82.jar")[span_46](end_span)
[span_47](start_span)val xposedApiSourcesJar = files("libs/api-82-sources.jar")[span_47](end_span)
[span_48](start_span)val xposedBridgeSourcesJar = files("libs/bridge-82-sources.jar")[span_48](end_span)

plugins {
    [span_49](start_span)id("com.android.application")[span_49](end_span)
    [span_50](start_span)id("org.jetbrains.kotlin.android")[span_50](end_span)
    [span_51](start_span)kotlin("plugin.serialization")[span_51](end_span) // Version managed by settings.gradle.kts
    [span_52](start_span)id("org.jetbrains.kotlin.plugin.parcelize")[span_52](end_span)
    [span_53](start_span)id("dagger.hilt.android.plugin")[span_53](end_span)
    [span_54](start_span)id("com.google.gms.google-services")[span_54](end_span)
    [span_55](start_span)id("androidx.navigation.safeargs.kotlin")[span_55](end_span)
    [span_56](start_span)id("org.jetbrains.compose")[span_56](end_span)
    [span_57](start_span)id("org.openapi.generator")[span_57](end_span)
    [span_58](start_span)id("com.google.devtools.ksp")[span_58](end_span) // Version managed by settings.gradle.kts
}

android {
    [span_59](start_span)namespace = "dev.aurakai.auraframefx"[span_59](end_span)
    [span_60](start_span)compileSdk = 36[span_60](end_span)

    defaultConfig {
        [span_61](start_span)applicationId = "dev.aurakai.auraframefx"[span_61](end_span)
        [span_62](start_span)minSdk = 33[span_62](end_span)
        [span_63](start_span)targetSdk = 36[span_63](end_span)
        [span_64](start_span)versionCode = 1[span_64](end_span)
        [span_65](start_span)versionName = "1.0"[span_65](end_span)

        [span_66](start_span)testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"[span_66](end_span)

        // Xposed configuration
        [span_67](start_span)buildConfigField("String", "XPOSED_API_VERSION", "\"82\"")[span_67](end_span)
        [span_68](start_span)buildConfigField("String", "LSPOSED_PACKAGE_NAME", "\"de.robv.android.xposed\"")[span_68](end_span)
        [span_69](start_span)buildConfigField("int", "LSPOSED_API_VERSION", "82")[span_69](end_span)

        // Room schema location for annotation processing
        [span_70](start_span)javaCompileOptions {[span_70](end_span)
            annotationProcessorOptions {
                arguments += mapOf(
                    [span_71](start_span)"room.schemaLocation" to "$projectDir/schemas",[span_71](end_span)
                    [span_72](start_span)"room.incremental" to "true",[span_72](end_span)
                    [span_73](start_span)"room.expandProjection" to "true",[span_73](end_span)
                    [span_74](start_span)"dagger.fastInit" to "enabled"[span_74](end_span)
                )
            }
        }
    }

    buildTypes {
        release {
            [span_75](start_span)isMinifyEnabled = true[span_75](end_span)
            [span_76](start_span)isShrinkResources = true[span_76](end_span)
            proguardFiles(
                [span_77](start_span)getDefaultProguardFile("proguard-android-optimize.txt"),[span_77](end_span)
                [span_78](start_span)"proguard-rules.pro"[span_78](end_span)
            )
        }
        debug {
            [span_79](start_span)isDebuggable = true[span_79](end_span)
        }
    }

    compileOptions {
        [span_80](start_span)sourceCompatibility = JavaVersion.VERSION_21[span_80](end_span)
        [span_81](start_span)targetCompatibility = JavaVersion.VERSION_21[span_81](end_span)
        [span_82](start_span)isCoreLibraryDesugaringEnabled = true[span_82](end_span)
    }

    lint {
        [span_83](start_span)checkDependencies = true[span_83](end_span)
        [span_84](start_span)lintConfig = file("lint.xml")[span_84](end_span)
        [span_85](start_span)ignoreTestSources = true[span_85](end_span)
        [span_86](start_span)abortOnError = false[span_86](end_span)
        [span_87](start_span)warningsAsErrors = true[span_87](end_span)
        [span_88](start_span)checkReleaseBuilds = false[span_88](end_span)
        [span_89](start_span)checkAllWarnings = true[span_89](end_span)

        disable.addAll(
            listOf(
                [span_90](start_span)"MissingTranslation",[span_90](end_span)
                [span_91](start_span)"VectorPath",[span_91](end_span)
                [span_92](start_span)"MissingIf"[span_92](end_span)
            )
        )
    }

    testOptions {
        [span_93](start_span)unitTests.isReturnDefaultValues = true[span_93](end_span)
    }

    kotlinOptions {
        [span_94](start_span)jvmTarget = "21"[span_94](end_span)
        [span_95](start_span)languageVersion = "1.9"[span_95](end_span)
        [span_96](start_span)apiVersion = "1.9"[span_96](end_span)
        freeCompilerArgs = listOf(
            [span_97](start_span)"-Xjvm-default=all",[span_97](end_span)
            [span_98](start_span)"-opt-in=kotlin.RequiresOptIn",[span_98](end_span)
            [span_99](start_span)"-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",[span_99](end_span)
            [span_100](start_span)"-opt-in=kotlinx.serialization.ExperimentalSerializationApi",[span_100](end_span)
            [span_101](start_span)"-opt-in=kotlin.time.ExperimentalTime",[span_101](end_span)
            [span_102](start_span)"-opt-in=kotlin.experimental.ExperimentalTypeInference",[span_102](end_span)
            [span_103](start_span)"-opt-in=kotlin.ExperimentalStdlibApi",[span_103](end_span)
            [span_104](start_span)"-opt-in=kotlin.concurrent.ExperimentalAtomicApi",[span_104](end_span)
            [span_105](start_span)"-opt-in=kotlin.experimental.ExperimentalNativeApi",[span_105](end_span)
            [span_106](start_span)"-Xcontext-receivers"[span_106](end_span)
        )
    }

    buildFeatures {
        [span_107](start_span)compose = true[span_107](end_span)
        [span_108](start_span)buildConfig = true[span_108](end_span)
        [span_109](start_span)viewBinding = true[span_109](end_span)
    }

    composeOptions {
        [span_110](start_span)kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()[span_110](end_span)
    }

    packaging {
        resources {
            [span_111](start_span)excludes += "/META-INF/{AL2.0,LGPL2.1}"[span_111](end_span)
            [span_112](start_span)excludes.add("META-INF/LICENSE.md")[span_112](end_span)
            [span_113](start_span)excludes.add("META-INF/LICENSE-notice.md")[span_113](end_span)
            [span_114](start_span)excludes.add("META-INF/licenses/**")[span_114](end_span)
            [span_115](start_span)excludes.add("META-INF/LICENSE*")[span_115](end_span)
        }
        jniLibs {
            [span_116](start_span)useLegacyPackaging = true[span_116](end_span)
        }
    }

    sourceSets {
        getByName("main") {
            [span_117](start_span)java.srcDir("build/generated/src/main/kotlin")[span_117](end_span)
        }
    }
    [span_118](start_span)buildToolsVersion = "36.0.0"[span_118](end_span)

    hilt {
        [span_119](start_span)enableAggregatingTask = true[span_119](end_span)
    }
}

kotlin {
    [span_120](start_span)jvmToolchain(21)[span_120](end_span)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    [span_121](start_span)jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.ERROR)[span_121](end_span)
}

// Xposed framework configuration - must be compileOnly as it's provided by the Xposed framework at runtime
[span_122](start_span)val xposedCompileOnly = configurations.create("xposedCompileOnly")[span_122](end_span)

dependencies {
    // Core Android
    [span_123](start_span)coreLibraryDesugaring(libs.desugarJdkLibs)[span_123](end_span)
    [span_124](start_span)implementation(libs.androidx.core.ktx)[span_124](end_span)
    [span_125](start_span)implementation(libs.androidx.appcompat)[span_125](end_span)
    
    // Protocol Buffers and Netty
    [span_126](start_span)implementation("com.google.protobuf:protobuf-java:3.25.5")[span_126](end_span)
    [span_127](start_span)implementation("commons-io:commons-io:2.19.0")[span_127](end_span)
    [span_128](start_span)implementation("io.netty:netty-codec-http2:4.2.2.Final")[span_128](end_span)
    [span_129](start_span)implementation("io.netty:netty-handler:4.1.118.Final")[span_129](end_span)
    [span_130](start_span)implementation("org.bouncycastle:bcprov-jdk18on:1.78")[span_130](end_span)
    [span_131](start_span)implementation("io.netty:netty-common:4.1.118.Final")[span_131](end_span)
    [span_132](start_span)implementation("org.apache.commons:commons-compress:1.26.0")[span_132](end_span)
    [span_133](start_span)implementation("com.google.guava:guava:33.4.8-android")[span_133](end_span)
    [span_134](start_span)implementation("io.netty:netty-codec-http:4.1.118.Final")[span_134](end_span)
    [span_135](start_span)implementation(libs.google.material)[span_135](end_span)
    [span_136](start_span)implementation(libs.androidx.constraintlayout)[span_136](end_span)
    [span_137](start_span)implementation(libs.androidx.lifecycle.runtime.ktx)[span_137](end_span)
    [span_138](start_span)implementation(libs.androidx.activity.compose)[span_138](end_span)

    // Kotlinx Serialization
    [span_139](start_span)implementation(libs.kotlinx.serialization.json)[span_139](end_span)
    [span_140](start_span)implementation(libs.kotlinx.serialization.xml)[span_140](end_span)

    // Dagger Hilt
    [span_141](start_span)implementation(libs.hilt.android)[span_141](end_span)
    [span_142](start_span)ksp(libs.hilt.compiler)[span_142](end_span)
    [span_143](start_span)implementation(libs.androidx.hilt.navigation.compose)[span_143](end_span)
    [span_144](start_span)implementation(libs.androidx.hilt.work)[span_144](end_span)

    // Kotlin Coroutines
    [span_145](start_span)implementation(libs.kotlinx.coroutines.core)[span_145](end_span)
    [span_146](start_span)implementation(libs.kotlinx.coroutines.android)[span_146](end_span)
    [span_147](start_span)implementation(libs.kotlinx.coroutines.play.services)[span_147](end_span)

    // Permissions (use Accompanist)
    [span_148](start_span)implementation(libs.accompanist.permissions)[span_148](end_span)

    // Compose
    [span_149](start_span)implementation(platform(libs.androidx.compose.bom))[span_149](end_span)
    [span_150](start_span)implementation(libs.androidx.compose.ui)[span_150](end_span)
    [span_151](start_span)implementation(libs.androidx.compose.ui.graphics)[span_151](end_span)
    [span_152](start_span)implementation(libs.androidx.compose.ui.tooling.preview)[span_152](end_span)
    [span_153](start_span)implementation(libs.androidx.compose.material3)[span_153](end_span)
    [span_154](start_span)implementation(libs.androidx.compose.foundation)[span_154](end_span)
    [span_155](start_span)implementation(libs.androidx.compose.runtime)[span_155](end_span)
    [span_156](start_span)debugImplementation(libs.androidx.compose.ui.tooling)[span_156](end_span)
    [span_157](start_span)debugImplementation(libs.androidx.compose.ui.test.manifest)[span_157](end_span)

    // Google Cloud
    [span_158](start_span)implementation(platform(libs.google.cloud.bom))[span_158](end_span)
    [span_159](start_span)implementation(libs.google.cloud.generativeai)[span_159](end_span)

    [span_160](start_span)androidTestImplementation(libs.androidx.compose.ui.test.junit4)[span_160](end_span)
    [span_161](start_span)androidTestImplementation(libs.androidx.test.ext.junit)[span_161](end_span)
    [span_162](start_span)testImplementation(libs.junit)[span_162](end_span)

    // Room
    [span_163](start_span)implementation(libs.androidx.room.runtime)[span_163](end_span)
    [span_164](start_span)implementation(libs.androidx.room.ktx)[span_164](end_span)
    [span_165](start_span)ksp(libs.androidx.room.compiler)[span_165](end_span)

    // Work Manager
    [span_166](start_span)implementation(libs.androidx.work.runtime.ktx)[span_166](end_span)

    // DataStore
    [span_167](start_span)implementation(libs.androidx.datastore.preferences)[span_167](end_span)

    // UI Components
    [span_168](start_span)implementation(libs.androidx.cardview)[span_168](end_span)
    [span_169](start_span)implementation(libs.coil.compose)[span_169](end_span)
    [span_170](start_span)implementation(libs.accompanist.systemuicontroller)[span_170](end_span)

    // Compose Glance
    [span_171](start_span)implementation(libs.glance.appwidget)[span_171](end_span)
    [span_172](start_span)implementation(libs.glance.compose)[span_172](end_span)

    // Firebase
    [span_173](start_span)implementation(platform(libs.firebase.bom))[span_173](end_span)
    [span_174](start_span)implementation(libs.firebase.analytics.ktx)[span_174](end_span)
    [span_175](start_span)implementation(libs.firebase.auth.ktx)[span_175](end_span)
    [span_176](start_span)implementation(libs.firebase.firestore.ktx)[span_176](end_span)
    [span_177](start_span)implementation(libs.firebase.storage)[span_177](end_span)
    [span_178](start_span)implementation(libs.firebase.crashlytics.lib)[span_178](end_span)

    [span_179](start_span)// Google Cloud AI - using BOM for version[span_179](end_span) management
    [span_180](start_span)implementation("com.google.cloud:google-cloud-generativeai")[span_180](end_span)

    // Timber
    [span_181](start_span)implementation(libs.timber)[span_181](end_span)
    [span_182](start_span)implementation(libs.retrofit)[span_182](end_span)
    [span_183](start_span)implementation(libs.retrofit.converter.gson)[span_183](end_span)
    [span_184](start_span)implementation(libs.okhttp)[span_184](end_span)
    [span_185](start_span)implementation(libs.okhttp.logging.interceptor)[span_185](end_span)
    [span_186](start_span)implementation(libs.retrofit.converter.kotlinx.serialization)[span_186](end_span)

    // Xposed dependencies - using local JARs
    [span_187](start_span)compileOnly(xposedApiJar)[span_187](end_span)
    [span_188](start_span)compileOnly(xposedBridgeJar)[span_188](end_span)

    // Xposed hidden API bypass
    [span_189](start_span)xposedCompileOnly(libs.xposed.hiddenapibypass)[span_189](end_span)

    // For development and documentation
    [span_190](start_span)compileOnly(xposedApiSourcesJar)[span_190](end_span) // Only needed for development
    [span_191](start_span)compileOnly(xposedBridgeSourcesJar)[span_191](end_span) // Only needed for development

    [span_192](start_span)// LSPosed API (if using LSPosed specific[span_192](end_span) features)
    [span_193](start_span)xposedCompileOnly("org.lsposed:libxposed:82")[span_193](end_span)
    [span_194](start_span)xposedCompileOnly("org.lsposed:libxposed:82:sources")[span_194](end_span) // For development only

    // Testing
    [span_195](start_span)testImplementation(libs.junit)[span_195](end_span)
    [span_196](start_span)testImplementation(libs.kotlinx.coroutines.test)[span_196](end_span)
    [span_197](start_span)testImplementation(libs.androidx.arch.core.testing)[span_197](end_span)
    [span_198](start_span)testImplementation(libs.mockito.core)[span_198](end_span)
    [span_199](start_span)testImplementation(libs.mockk)[span_199](end_span)

    [span_200](start_span)androidTestImplementation(libs.androidx.espresso.core)[span_200](end_span)
}

openApiGenerate {
    [span_201](start_span)generatorName.set("kotlin")[span_201](end_span)
    [span_202](start_span)inputSpec.set("$projectDir/src/main/resources/auraframefx_ai_api.yaml")[span_202](end_span)
    [span_203](start_span)outputDir.set(layout.buildDirectory.dir("generated/openapi").map { it.asFile.absolutePath })[span_203](end_span)
    [span_204](start_span)apiPackage.set("dev.aurakai.auraframefx.generated.api.auraframefxai")[span_204](end_span)
    [span_205](start_span)modelPackage.set("dev.aurakai.auraframefx.generated.model.auraframefxai")[span_205](end_span)
    configOptions.set(
        mapOf(
            [span_206](start_span)"library" to "jvm-retrofit2",[span_206](end_span)
            [span_207](start_span)"serializationLibrary" to "kotlinx_serialization",[span_207](end_span)
            [span_208](start_span)"useCoroutines" to "true",[span_208](end_span)
            [span_209](start_span)"dateLibrary" to "java8",[span_209](end_span)
            [span_210](start_span)"enumPropertyNaming" to "UPPERCASE"[span_210](end_span)
        )
    )
}

tasks.register("validateOpenApiSpec") {
    [span_211](start_span)val specFile = file("$projectDir/src/main/resources/auraframefx_ai_api.yaml")[span_211](end_span)
    doLast {
        if (!specFile.exists()) {
            [span_212](start_span)logger.warn("API spec file not found at: ${specFile.absolutePath}")[span_212](end_span)
            [span_213](start_span)logger.warn("OpenAPI code generation may fail or use stale code.")[span_213](end_span)
        } else {
            [span_214](start_span)logger.lifecycle("OpenAPI spec file found at: ${specFile.absolutePath}")[span_214](end_span)
        }
    }
}

tasks.named("openApiGenerate") {
    [span_215](start_span)dependsOn("validateOpenApiSpec")[span_215](end_span)
    [span_216](start_span)mustRunAfter(tasks.named("clean"))[span_216](end_span)
}

tasks.register<Delete>("cleanOpenApiGenerated") {
    [span_217](start_span)delete(layout.buildDirectory.dir("generated/openapi"))[span_217](end_span)
}

tasks.named("clean") {
    [span_218](start_span)finalizedBy("cleanOpenApiGenerated")[span_218](end_span)
}

tasks.named("preBuild") {
    [span_219](start_span)dependsOn("openApiGenerate")[span_219](end_span)
}
