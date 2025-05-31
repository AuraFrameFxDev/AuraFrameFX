package dev.aurakai.auraframefx

class NativeLib {
    /**
     * A native method that is implemented by the 'aura-fx-lib' native library,
     * which is packaged with this application.
     *
     * Temporarily returning mock value for debug builds.
     */
    // external fun stringFromJNI(): String
    fun stringFromJNI(): String {
        return "Mock Native Response (Debug Build)"
    }

    companion object {
        // Used to load the 'aura-fx-lib' library on application startup.
        // Temporarily disabled for debug builds
        init {
            // Native library loading disabled for debug build
        }
    }
}
