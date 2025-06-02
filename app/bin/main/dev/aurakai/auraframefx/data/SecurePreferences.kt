package dev.aurakai.auraframefx.data

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.KeyStore
import javax.crypto.KeyGenerator

class SecurePreferences(private val context: Context) {
    private val masterKeyAlias: String by lazy {
        try {
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw SecurityException("Failed to create master key", e)
        }
    }

    private val prefs: SharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                "secure_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw SecurityException("Failed to create encrypted preferences", e)
        }
    }

    fun saveOAuthToken(token: String) {
        try {
            prefs.edit().putString("oauth_token", token).apply()
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw SecurityException("Failed to save OAuth token", e)
        }
    }

    fun getOAuthToken(): String? {
        try {
            return prefs.getString("oauth_token", null)
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw SecurityException("Failed to get OAuth token", e)
        }
    }

    fun clearOAuthToken() {
        try {
            prefs.edit().remove("oauth_token").apply()
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw SecurityException("Failed to clear OAuth token", e)
        }
    }

    fun saveApiToken(token: String) {
        try {
            prefs.edit().putString("api_key", token).apply()
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw SecurityException("Failed to save API token", e)
        }
    }

    fun getApiToken(): String? {
        try {
            return prefs.getString("api_key", null)
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw SecurityException("Failed to get API token", e)
        }
    }

    fun clearApiToken() {
        try {
            prefs.edit().remove("api_key").apply()
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw SecurityException("Failed to clear API token", e)
        }
    }

    fun clearAllTokens() {
        try {
            clearOAuthToken()
            clearApiToken()
        } catch (e: Exception) {
            e.printStackTrace()
            cleanupResources()
            throw SecurityException("Failed to clear all tokens", e)
        }
    }

    private fun cleanupResources() {
        try {
            // Clear all preferences
            prefs.edit().clear().apply()

            // Delete preference file
            val prefsFile = File(context.filesDir, "shared_prefs/secure_prefs.xml")
            if (prefsFile.exists()) {
                prefsFile.delete()
            }

            // Delete backup files
            val backupDir = File(context.filesDir, "backup")
            if (backupDir.exists()) {
                backupDir.listFiles()?.forEach { file ->
                    if (file.name.startsWith("secure_prefs")) {
                        file.delete()
                    }
                }
            }

            // Delete key from keystore
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            keyStore.deleteEntry("api_token_key")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun initialize(context: Context) {
            try {
                // Initialize the Android Keystore
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)

                // Create a key pair for encryption
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore"
                )

                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    "api_token_key",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()

                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()

            } catch (e: Exception) {
                e.printStackTrace()
                cleanupResources()
                throw SecurityException("Failed to initialize secure preferences", e)
            }
        }
    }
}