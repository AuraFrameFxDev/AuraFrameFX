import android.app.Application
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

--- a/auraframe.zip/app/src/main/java/dev/aurakai/auraframefx/AuraFrameApplication.kt
+++ b/auraframe.zip/app/src/main/java/dev/aurakai/auraframefx/AuraFrameApplication.kt
@@ -1,13 +1,18 @@
package dev.aurakai.auraframefx

import android.app.Application

-import com.google.firebase.FirebaseApp

+import androidx.hilt.work.HiltWorkerFactory

+import androidx.work.Configuration

+import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp

+import javax.inject.Inject

@HiltAndroidApp
-class AuraFrameApplication : Application() {
    +
    class AuraFrameApplication : Application(), Configuration.Provider {
        +
        override fun onCreate() {
            super.onCreate()
            -FirebaseApp.initializeApp(this)
            +        // FirebaseApp.initializeApp(this) // This is typically handled by MainApplication.kt with Hilt
        }
        +
        override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder().build()
    }