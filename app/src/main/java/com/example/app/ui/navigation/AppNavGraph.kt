package com.example.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app.ui.screens.AiChatScreen
import com.example.app.ui.screens.HomeScreen
import com.example.app.ui.screens.ProfileScreen
import com.example.app.ui.screens.SettingsScreen

/**
 * Main navigation graph for the AuraFrameFX app
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Home.route
    ) {
        composable(NavDestination.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(NavDestination.AiChat.route) {
            AiChatScreen()
        }
        
        composable(NavDestination.Profile.route) {
            ProfileScreen()
        }
        
        composable(NavDestination.Settings.route) {
            SettingsScreen()
        }
        
        // Add more composable destinations as needed
    }
}
