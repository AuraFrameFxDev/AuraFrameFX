// build.gradle.kts (Project Root) - The Correct Version

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false // CRITICAL: This must be here
    alias(libs.plugins.kotlin.serialization) apply false // This must also be here
    alias(libs.plugins.openapi.generator) apply false
    // Add Compose plugin
    alias(libs.plugins.kotlin.compose) apply false
}