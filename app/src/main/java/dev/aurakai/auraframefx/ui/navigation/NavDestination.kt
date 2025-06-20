package dev.aurakai.auraframefx.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import dev.aurakai.auraframefx.ui.theme.AppStrings

/**
 * Navigation destinations for the app
 */
public sealed class NavDestination(
    public val route: String,
    public val title: String,
    public val icon: ImageVector? = null,
) {
    public object Home : NavDestination(
        route = "home",
        title = AppStrings.NAV_HOME,
        icon = Icons.Default.Home
    )

    public object AiChat : NavDestination(
        route = "ai_chat",
        title = AppStrings.NAV_AI_CHAT,
        icon = Icons.Default.Chat
    )

    public object Profile : NavDestination(
        route = "profile",
        title = AppStrings.NAV_PROFILE,
        icon = Icons.Default.Person
    )

    public object Settings : NavDestination(
        route = "settings",
        title = AppStrings.NAV_SETTINGS,
        icon = Icons.Default.Settings
    )

    public object OracleDriveControl : NavDestination(
        route = "oracle_drive_control",
        title = "OracleDrive Control", // You may want to localize this
        icon = null // Optionally add an icon
    )

    // Add more destinations as needed

    public companion object {
        public val bottomNavItems = listOf(Home, AiChat, Profile, Settings)
        public val all = listOf(Home, AiChat, Profile, Settings, OracleDriveControl)
    }
}
