package com.example.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.example.app.ui.theme.AppDimensions
import com.example.app.ui.theme.AppStrings

/**
 * Settings screen for the AuraFrameFX app
 */
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppDimensions.spacing_medium)
    ) {
        Text(
            text = AppStrings.NAV_SETTINGS,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(AppDimensions.spacing_medium))
        
        SettingsCard(
            title = AppStrings.SETTINGS_THEME,
            description = "Switch between light and dark theme"
        ) {
            var darkThemeEnabled by remember { mutableStateOf(true) }
            Switch(
                checked = darkThemeEnabled,
                onCheckedChange = { darkThemeEnabled = it }
            )
        }
        
        Spacer(modifier = Modifier.height(AppDimensions.spacing_medium))
        
        SettingsCard(
            title = AppStrings.SETTINGS_NOTIFICATIONS,
            description = "Enable or disable push notifications"
        ) {
            var notificationsEnabled by remember { mutableStateOf(true) }
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }
        
        Spacer(modifier = Modifier.height(AppDimensions.spacing_medium))
        
        SettingsCard(
            title = AppStrings.SETTINGS_PRIVACY,
            description = "Manage your privacy settings"
        ) {
            var privacyEnabled by remember { mutableStateOf(false) }
            Switch(
                checked = privacyEnabled,
                onCheckedChange = { privacyEnabled = it }
            )
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(AppDimensions.spacing_medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                content()
            }
        }
    }
}
