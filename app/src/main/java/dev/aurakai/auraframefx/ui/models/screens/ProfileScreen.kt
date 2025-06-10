package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dev.aurakai.auraframefx.ui.theme.AppDimensions

/**
 * Profile screen for the AuraFrameFX app
 */
/**
 * Displays the user profile screen with a centered icon, title, and placeholder details.
 *
 * Presents a vertically centered layout featuring a large person icon, a "User Profile" headline, and a placeholder message for profile details, styled according to the app's theme.
 */
@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppDimensions.spacing_medium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            modifier = Modifier.size(AppDimensions.icon_size_large * 3),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacing_medium))

        Text(
            text = "User Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacing_medium))

        Text(
            text = "Profile details will be displayed here",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
