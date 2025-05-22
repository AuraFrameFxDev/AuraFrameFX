package dev.aurakai.auraframefx.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

@Composable
fun AuraNavHost(backendUrl: String, idToken: String) {
    val auraMoodViewModel: AuraMoodViewModel = viewModel()

    var currentScreen by remember { mutableStateOf<String>("Menu") }
    var orbOffset by remember { mutableStateOf<Offset>(Offset.Zero) }

    // Handle system back gestures
    BackHandler(enabled = currentScreen != "Menu") {
        currentScreen = "Menu"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (currentScreen) {
            "Menu" -> MenuScreen(
                onMenuSelected = { currentScreen = it },
                onPlayground = { currentScreen = "Playground" })

            "UI ENGINE" -> AIFeaturesScreen(backendUrl = backendUrl, idToken = idToken)
            "KaiToolbox" -> dev.aurakai.auraframefx.ui.security.KaiToolboxScreen(
                onBack = { currentScreen = "Menu" })

            "AurakaiEcoSys" -> auraMoodViewModel.AurakaiEcoSysScreen(
                backendUrl = backendUrl,
                idToken = idToken,
                onBack = { currentScreen = "Menu" })

            "Conference Room" -> PlaceholderScreen(
                screenName = "Conference Room (multi-agent chat)",
                onBack = { currentScreen = "Menu" })

            "Xhancement" -> PlaceholderScreen(
                screenName = "Xhancement (Xposed-style features)",
                onBack = { currentScreen = "Menu" })

            "Community" -> PlaceholderScreen(
                screenName = "Community (coming soon)",
                onBack = { currentScreen = "Menu" })

            "Help Desk" -> PlaceholderScreen(
                screenName = "Help Desk (coming soon)",
                onBack = { currentScreen = "Menu" })

            "Playground" -> CascadeZOrderPlayground()
        }
        // Draggable orb remains for all screens except Playground
        if (currentScreen != "Playground") {
            Box(
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.BottomEnd)
                    .offset { IntOffset(orbOffset.x.roundToInt(), orbOffset.y.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            orbOffset += dragAmount
                        }
                    }
            ) {
                StaticOrb()
            }
        }
    }
}
