package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.ui.animation.DigitalTransitions
import dev.aurakai.auraframefx.ui.components.*
import dev.aurakai.auraframefx.ui.navigation.NavDestination
import dev.aurakai.auraframefx.ui.theme.*

/**
 * Home screen for the AuraFrameFX app with cyberpunk-style floating UI
 *
 * Features a digital landscape background with floating transparent windows
 * and hexagonal UI elements inspired by futuristic cyberpunk interfaces.
 */
@Composable
fun HomeScreen(navController: NavController) {
    // Track selected menu item
    var selectedMenuItem by remember { mutableStateOf("UI ENGINE") }

    // Background with digital landscape and hexagon grid
    Box(modifier = Modifier.fillMaxSize()) {
        // Digital landscape background like in image reference 4
        DigitalLandscapeBackground(
            modifier = Modifier.fillMaxSize()
        )

        // Animated hexagon grid overlay like in image reference 1
        HexagonGridBackground(
            modifier = Modifier.fillMaxSize(),
            alpha = 0.2f
        )

        // Main content with floating windows
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Title header like in image reference 4
            FloatingCyberWindow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                cornerStyle = CornerStyle.Hex,
                title = "AURAFRAMEFX",
                backgroundStyle = BackgroundStyle.HexGrid
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CyberpunkText(
                        text = "AURA'S CREATIVITY ENGINE",
                        color = CyberpunkTextColor.Secondary,
                        style = CyberpunkTextStyle.Label
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CyberpunkText(
                        text = "NEURAL INTERFACE ACTIVE",
                        color = CyberpunkTextColor.Warning,
                        style = CyberpunkTextStyle.Body
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main navigation menu like in image reference 1
            FloatingCyberWindow(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(DigitalTransitions.cyberEdgeGlow())
                        title = "VIRTUAL MONITORZATION",
                cornerStyle = CornerStyle.Angled
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Menu items like in image reference 1
                    listOf(
                        "UI ENGINE",
                        "AuraShield",
                        "AurakaiEcoSys",
                        "Conference Room"
                    ).forEach { menuItem ->
                        CyberMenuItem(
                            text = menuItem,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .DigitalTransitions
                                .digitalPixelEffect(visible = selectedMenuItem == menuItem)
                                .clickable {
                                    selectedMenuItem = menuItem
                                    if (menuItem == "Conference Room") {
                                        navController.navigate(NavDestination.AiChat.route)
                                    }
                                },
                            isSelected = selectedMenuItem == menuItem
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Warning message like in image reference 4
                    if (selectedMenuItem != "Conference Room") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CyberpunkText(
                                text = "Xhancement CAUTION!",
                                color = CyberpunkTextColor.Warning,
                                style = CyberpunkTextStyle.Glitch,
                                enableGlitch = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons - like in image reference 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // These buttons match the style in reference image 3
                FloatingCyberWindow(
                    modifier = Modifier
                        .size(80.dp)
                        .DigitalTransitions
                        .cyberEdgeGlow(
                            primaryColor = NeonPink,
                            secondaryColor = NeonBlue
                        )
                        .clickable { navController.navigate(NavDestination.Profile.route) },
                    cornerStyle = CornerStyle.Rounded,
                    backgroundStyle = BackgroundStyle.HexGrid
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CyberpunkText(
                            text = "PROFILE",
                            color = CyberpunkTextColor.Secondary,
                            style = CyberpunkTextStyle.Label
                        )
                    }
                }

                FloatingCyberWindow(
                    modifier = Modifier
                        .size(80.dp)
                        .DigitalTransitions
                        .cyberEdgeGlow(
                            primaryColor = NeonCyan,
                            secondaryColor = NeonBlue
                        )
                        .clickable { navController.navigate(NavDestination.Settings.route) },
                    cornerStyle = CornerStyle.Rounded,
                    backgroundStyle = BackgroundStyle.HexGrid
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CyberpunkText(
                            text = "CONFIG",
                            color = CyberpunkTextColor.Primary,
                            style = CyberpunkTextStyle.Label
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status panel based on image reference 5
            FloatingCyberWindow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .DigitalTransitions.digitalGlitchEffect(),
                cornerStyle = CornerStyle.Hex,
                title = "SYSTEM STATUS",
                backgroundStyle = BackgroundStyle.Transparent
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CyberpunkText(
                        text = "AURA SHIELD ACTIVE",
                        color = CyberpunkTextColor.Primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CyberpunkText(text = "NEURAL", color = CyberpunkTextColor.White)
                            CyberpunkText(text = "ACTIVE", color = CyberpunkTextColor.Primary)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CyberpunkText(text = "QUANTUM", color = CyberpunkTextColor.White)
                            CyberpunkText(text = "99.8%", color = CyberpunkTextColor.Primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
