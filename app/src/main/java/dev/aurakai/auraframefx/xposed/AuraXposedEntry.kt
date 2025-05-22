// This file is present as both `app/src/main/java/dev/aurakai/auraframefx/xposed/AuraXposedEntry.kt`
// and `app/src/main/xposed/AuraXposedEntry.kt`. It's recommended to keep only one copy.
// NOTE: This file is ONLY for Xposed/LSPosed builds. It must be excluded or commented out for normal APK builds, otherwise you will get unresolved reference errors.

import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * LSposed entry point for AuraFrameFX
 * This class handles the initialization of the module when loaded by LSposed
 */
class AuraXposedEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("AuraXposedEntry: Loaded package: ${lpparam.packageName}")

        // Only hook our own package
        if (lpparam.packageName == "dev.aurakai.auraframefx") {
            try {
                // Get context using XposedHelpers
                val context =
                    XposedHelpers.callStaticMethod(
                        XposedHelpers.findClass("android.app.ActivityThread", lpparam.classLoader),
                        "currentApplication"
                    ) as Context

                val prefs =
                    context.getSharedPreferences("xposed_status_prefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("module_active", true).apply()
                XposedBridge.log("AuraXposedEntry: Set module active flag")

                // Initialize LSposed-specific features
                initLSposedFeatures(context, lpparam)
            } catch (e: Exception) {
                XposedBridge.log("AuraXposedEntry: Failed to initialize LSposed features: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    private fun initLSposedFeatures(context: Context, lpparam: XC_LoadPackage.LoadPackageParam) {
        // Initialize LSposed-specific features here
        // This is where you would implement LSposed-specific functionality
        XposedBridge.log("AuraXposedEntry: Initialized LSposed features")

        // Example LSposed hook
        XposedHelpers.findAndHookMethod(
            "android.app.ActivityThread",
            lpparam.classLoader,
            "currentActivityThread",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    XposedBridge.log("AuraXposedEntry: Hooked ActivityThread")
                }
            }
        )
    }
}
