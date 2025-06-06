package com.example.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.app.ui.navigation.NavDestination
import com.example.app.ui.theme.AppDimensions

/**
 * Home screen for the AuraFrameFX app
 */
/**
 * Displays the home screen UI for the AuraFrameFX app with a welcome message and navigation to the AI chat screen.
 *
 * Presents a centered layout featuring a headline, subtitle, and a button that navigates to the AI chat screen when clicked.
 */
@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppDimensions.spacing_medium),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to AuraFrameFX",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(AppDimensions.spacing_medium))
            
            Text(
                text = "Your cyberpunk-themed AI companion",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(AppDimensions.spacing_large))
            
            Button(
                onClick = { 
                    navController.navigate(NavDestination.AiChat.route)
                }
            ) {
                Text("Start Chatting with AI")
            }
        }
    }
}
