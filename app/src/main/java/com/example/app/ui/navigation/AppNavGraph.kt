package com.example.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app.ui.animation.DigitalTransitions.EnterDigitalMaterialization
import com.example.app.ui.animation.DigitalTransitions.ExitDigitalDematerialization
import com.example.app.ui.screens.AiChatScreen
import com.example.app.ui.screens.HomeScreen
import com.example.app.ui.screens.ProfileScreen
import com.example.app.ui.screens.SettingsScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

/**
 * Main navigation graph for the AuraFrameFX app with digital transition animations
 *
 * Sets up the main navigation graph for the AuraFrameFX app using Jetpack Compose with custom
 * cyberpunk-style digital materialization/dematerialization transitions between screens.
 * Uses Jetpack Navigation 3's built-in animation support for seamless screen transitions.
 *
 * @param navController The navigation controller used to manage app navigation.
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Home.route
    ) {
        composable(
            route = NavDestination.Home.route,
            enterTransition = { 
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) + 
                fadeIn(animationSpec = tween(300)) + 
                EnterDigitalMaterialization 
            },
            exitTransition = { 
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) + 
                fadeOut(animationSpec = tween(300)) + 
                ExitDigitalDematerialization 
            }
        ) {
            HomeScreen(navController = navController)
        }
        
        composable(
            route = NavDestination.AiChat.route,
            enterTransition = { 
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) + 
                fadeIn(animationSpec = tween(300)) + 
                EnterDigitalMaterialization 
            },
            exitTransition = { 
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) + 
                fadeOut(animationSpec = tween(300)) + 
                ExitDigitalDematerialization 
            }
        ) {
            AiChatScreen()
        }
        
        composable(
            route = NavDestination.Profile.route,
            enterTransition = { 
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) + 
                fadeIn(animationSpec = tween(300)) + 
                EnterDigitalMaterialization 
            },
            exitTransition = { 
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) + 
                fadeOut(animationSpec = tween(300)) + 
                ExitDigitalDematerialization 
            }
        ) {
            ProfileScreen()
        }
        
        composable(
            route = NavDestination.Settings.route,
            enterTransition = { 
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down) + 
                fadeIn(animationSpec = tween(300)) + 
                EnterDigitalMaterialization 
            },
            exitTransition = { 
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up) + 
                fadeOut(animationSpec = tween(300)) + 
                ExitDigitalDematerialization 
            }
        ) {
            SettingsScreen()
        }
        
        // Add more composable destinations as needed
    }
}
