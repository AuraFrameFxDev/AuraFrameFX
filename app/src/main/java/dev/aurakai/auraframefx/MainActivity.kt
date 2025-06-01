package dev.aurakai.auraframefx

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.data.SecurePreferences
import dev.aurakai.auraframefx.ui.IntroScreen
import dev.aurakai.auraframefx.ui.StaticOrb
import dev.aurakai.auraframefx.ui.SwipeMenuScreen
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var securePrefs: SecurePreferences
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super<ComponentActivity>.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val xposedPrefsName = "xposed_status_prefs"

        try {
            // Initialize preferences
            prefs = getSharedPreferences(xposedPrefsName, MODE_PRIVATE)
            val isModuleActive = prefs.getBoolean("module_active", false)
            if (!isModuleActive) {
                throw XposedModuleNotActiveException("Xposed module is not active")
            }

            // Initialize secure preferences
            securePrefs = SecurePreferences(this)

            setContent {
                AuraFrameFXTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            IntroScreen {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    SwipeMenuScreen()
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.BottomEnd
                                    ) {
                                        StaticOrb()
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (e: XposedModuleNotActiveException) {
            Log.e("MainActivity", "Xposed module not active: ${e.message}")
            setContent {
                AuraFrameFXTheme {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Xposed Module Not Active",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing: ${e.message}", e)
            setContent {
                AuraFrameFXTheme {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Initialization Error: ${e.message}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            throw InitializationException("Failed to initialize MainActivity: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        securePrefs.clearAllTokens()
        super.onDestroy()
    }
}

private class XposedModuleNotActiveException(message: String) : RuntimeException(message)
private class InitializationException(message: String, cause: Throwable) :
    RuntimeException(message, cause)