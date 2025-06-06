package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.material3.MaterialTheme // No longer directly used here
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.app.ui.theme.AuraFrameFXTheme // Added import

class MainActivity : ComponentActivity() { // Changed parent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Removed TODO about unused entry point, as it's now used for Compose content.
        setContent {
            AuraFrameFXTheme { // Using the new custom theme
                Greeting("Welcome to AuraFrameFX!")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: Reported as unused entry point by static analysis, but it's a valid lifecycle method.
        // TODO: Perform any cleanup here if needed.
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Hello $name!")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AuraFrameFXTheme { // Using the new custom theme for preview
        Greeting("AuraFrameFX Preview")
    }
}
