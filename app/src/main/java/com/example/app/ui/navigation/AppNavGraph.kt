package com.example.app.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.app.ui.animation.DigitalTransitions.EnterDigitalMaterialization
import com.example.app.ui.animation.DigitalTransitions.ExitDigitalDematerialization
import com.example.app.ui.screens.AiChatScreen
import com.example.app.ui.screens.HomeScreen
import com.example.app.ui.screens.ProfileScreen
import com.example.app.ui.screens.SettingsScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

/**
 * Main navigation graph for the AuraFrameFX app with digital transition animations
 *
 * Sets up the main navigation graph for the AuraFrameFX app using Jetpack Compose with custom
 * cyberpunk-style digital materialization/dematerialization transitions between screens.
 *
 * @param navController The animation-enabled navigation controller used to manage app navigation.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = NavDestination.Home.route
    ) {
        composable(
            route = NavDestination.Home.route,
            enterTransition = { EnterDigitalMaterialization },
            exitTransition = { ExitDigitalDematerialization }
        ) {
            HomeScreen(navController = navController)
        }
        
        composable(
            route = NavDestination.AiChat.route,
            enterTransition = { EnterDigitalMaterialization },
            exitTransition = { ExitDigitalDematerialization }
        ) {
            AiChatScreen()
        }
        
        composable(
            route = NavDestination.Profile.route,
            enterTransition = { EnterDigitalMaterialization },
            exitTransition = { ExitDigitalDematerialization }
        ) {
            ProfileScreen()
        }
        
        composable(
            route = NavDestination.Settings.route,
            enterTransition = { EnterDigitalMaterialization },
            exitTransition = { ExitDigitalDematerialization }
        ) {
            SettingsScreen()
        }
        
        // Add more composable destinations as needed
    }
}
