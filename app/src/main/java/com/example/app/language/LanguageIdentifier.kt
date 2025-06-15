package com.example.app.language

import android.content.Context

/**
 * Wrapper class for the language identification JNI library.
 * This class provides a Kotlin-friendly API for language detection.
 */
class LanguageIdentifier private constructor(context: Context) {

    companion object {
        // Load the native library
        init {
            System.loadLibrary("language_id_l2c_jni")
        }

        @Volatile
        private var instance: LanguageIdentifier? = null

        /**
         * Get the singleton instance of LanguageIdentifier
         */
        fun getInstance(context: Context): LanguageIdentifier {
            return instance ?: synchronized(this) {
                instance ?: LanguageIdentifier(context.applicationContext).also { instance = it }
            }
        }
    }

    private external fun nativeInitialize(modelPath: String): Long
    private external fun nativeDetectLanguage(handle: Long, text: String): String
    private external fun nativeRelease(handle: Long)
    private external fun nativeGetVersion(): String

    private var nativeHandle: Long = 0
    private var isInitialized = false

    init {
        // Initialize with default model
        try {
            // Copy the model file from assets to internal storage if needed
            // Then initialize with the model path
            nativeHandle = nativeInitialize("")
            isInitialized = true
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize LanguageIdentifier", e)
        }
    }

    /**
     * Detect the language of the given text
     * @param text The text to analyze
     * @return The detected language code (e.g., "en", "es", "fr")
     */
    fun detectLanguage(text: String): String {
        if (!isInitialized) throw IllegalStateException("LanguageIdentifier not initialized")
        return try {
            nativeDetectLanguage(nativeHandle, text)
        } catch (e: Exception) {
            "und" // Return 'und' for undetermined if detection fails
        }
    }

    /**
     * Get the version of the native library
     */
    fun getVersion(): String {
        return try {
            nativeGetVersion()
        } catch (e: Exception) {
            "unknown"
        }
    }

    /**
     * Clean up native resources
     */
    fun release() {
        if (isInitialized) {
            nativeRelease(nativeHandle)
            nativeHandle = 0
            isInitialized = false
        }
    }

    protected fun finalize() {
        release()
    }
}
