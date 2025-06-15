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
         * Returns the singleton instance of LanguageIdentifier, initializing it with the application context if necessary.
         *
         * @return The singleton LanguageIdentifier instance.
         */
        fun getInstance(context: Context): LanguageIdentifier {
            return instance ?: synchronized(this) {
                instance ?: LanguageIdentifier(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
 * Initializes the native language identifier with the specified model path.
 *
 * @param modelPath Path to the language identification model file.
 * @return A native handle representing the initialized language identifier.
 */
private external fun nativeInitialize(modelPath: String): Long
    /**
 * Detects the language of the given text using the native language identification library.
 *
 * @param handle Native handle to the language identifier instance.
 * @param text The input text to analyze.
 * @return The detected language code as a string.
 */
private external fun nativeDetectLanguage(handle: Long, text: String): String
    /**
 * Releases native resources associated with the specified handle.
 *
 * @param handle The native handle to release.
 */
private external fun nativeRelease(handle: Long)
    /**
 * Retrieves the version string of the native language identification library.
 *
 * @return The version of the native library.
 */
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
     * Detects the language of the provided text using the native language identification model.
     *
     * @param text The input text to analyze.
     * @return The ISO language code of the detected language, or "und" if detection fails or is undetermined.
     * @throws IllegalStateException if the language identifier has not been initialized.
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
     * Returns the version string of the native language identification library.
     *
     * @return The version string, or "unknown" if the version cannot be determined.
     */
    fun getVersion(): String {
        return try {
            nativeGetVersion()
        } catch (e: Exception) {
            "unknown"
        }
    }

    /**
     * Releases native resources associated with the language identifier instance.
     *
     * After calling this method, the instance becomes uninitialized and cannot be used for language detection until reinitialized.
     */
    fun release() {
        if (isInitialized) {
            nativeRelease(nativeHandle)
            nativeHandle = 0
            isInitialized = false
        }
    }

    /**
     * Ensures native resources are released when the object is garbage collected.
     */
    protected fun finalize() {
        release()
    }
}
