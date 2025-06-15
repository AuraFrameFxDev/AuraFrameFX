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
         * Retrieves the singleton instance of LanguageIdentifier, initializing it with the application context if it does not already exist.
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
 * Initializes the native language identifier using the provided model file path.
 *
 * @param modelPath The file system path to the language identification model.
 * @return A native handle to the initialized language identifier instance, or 0 if initialization fails.
 */
private external fun nativeInitialize(modelPath: String): Long
    /**
 * Uses the native language identification library to detect the language of the provided text.
 *
 * @param handle Native handle referencing the language identifier instance.
 * @param text The text to analyze for language detection.
 * @return The ISO language code detected for the input text, or "und" if detection fails.
 */
private external fun nativeDetectLanguage(handle: Long, text: String): String
    /**
 * Frees native resources associated with the given native handle.
 *
 * This method should be called to prevent memory leaks when the native language identifier is no longer needed.
 *
 * @param handle The native handle whose resources will be released.
 */
private external fun nativeRelease(handle: Long)
    /**
 * Returns the version string of the underlying native language identification library.
 *
 * @return The native library version.
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
     * Identifies the language of the given text and returns its ISO language code.
     *
     * Returns "und" if the language cannot be determined or if detection fails.
     *
     * @param text The text to analyze for language identification.
     * @return The ISO language code of the detected language, or "und" if undetermined.
     * @throws IllegalStateException if the language identifier is not initialized.
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
     * Retrieves the version string of the native language identification library.
     *
     * @return The version string, or "unknown" if retrieval fails.
     */
    fun getVersion(): String {
        return try {
            nativeGetVersion()
        } catch (e: Exception) {
            "unknown"
        }
    }

    /**
     * Releases native resources held by this language identifier instance.
     *
     * After calling this method, the instance is no longer initialized and cannot perform language detection until reinitialized.
     */
    fun release() {
        if (isInitialized) {
            nativeRelease(nativeHandle)
            nativeHandle = 0
            isInitialized = false
        }
    }

    /**
     * Releases native resources when the object is garbage collected.
     *
     * This method is called by the garbage collector before the object is removed from memory,
     * ensuring that any associated native resources are properly freed.
     */
    protected fun finalize() {
        release()
    }
}
