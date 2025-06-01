# Firebase general rules (often automatically included by SDKs, but good to keep if issues arise)
-keepattributes Signature
-keepclassmembers class * {
    @org.checkerframework.checker.nullness.qual.Nullable <fields>;
}

# If using Realtime Database with custom model classes (POJOs)
# Replace 'com.yourcompany.models.**' with your actual package.
-keepclassmembers class com.yourcompany.models.** { *; }

# For Crashlytics with de-obfuscated reports
-keepattributes SourceFile,LineNumberTable

# Hilt general rules (often automatically included by Hilt, but explicit here for clarity)
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep specific generated classes if needed (e.g., if you have issues with ViewModel names)
# You might find rules like these in issue trackers, but try without them first.
# Example: -keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel