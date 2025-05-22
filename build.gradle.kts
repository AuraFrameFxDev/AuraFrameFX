buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0") // Hardcoded Android Gradle Plugin version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0") // Hardcoded Kotlin Gradle Plugin version
        classpath("com.google.gms:google-services:4.4.0") // Hardcoded Google Services version
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9") // Hardcoded Crashlytics version
    }
}