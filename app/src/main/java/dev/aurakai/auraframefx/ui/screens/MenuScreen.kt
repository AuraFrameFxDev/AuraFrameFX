package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionType
import dev.aurakai.auraframefx.ui.components.HologramTransition

@Composable
fun DigitalTransitionRow(
    currentType: HomeScreenTransitionType,
    onTypeSelected: (HomeScreenTransitionType) -> Unit
) {
    val options = HomeScreenTransitionType.values()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = "Transition Style:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEach { transitionType ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (transitionType == currentType),
                            onClick = { onTypeSelected(transitionType) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (transitionType == currentType),
                        onClick = null  // null because we're handling clicks on the row
                    )
                    Text(
                        text = transitionType.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MenuScreen(
    transitionType: HomeScreenTransitionType = HomeScreenTransitionType.DIGITAL_DECONSTRUCT,
    showHologram: Boolean = true
) {
    HologramTransition(visible = showHologram) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Main Menu",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary, // Explicitly use primary color
                modifier = Modifier.padding(bottom = 24.dp)
            )
            // Digital transition row always shown for menu customization
            DigitalTransitionRow(
                currentType = transitionType,
                onTypeSelected = {}
            )
            Button(
                onClick = { /* TODO: Handle Menu Item 1 click */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary, // Explicitly use secondary
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Menu Item 1 (Themed)")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: Handle Menu Item 2 click */ }) {
                Text("Menu Item 2")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: Handle Menu Item 3 click */ }) {
                Text("Menu Item 3")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: Handle Settings click */ }) {
                Text("Settings")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    MaterialTheme { // Using MaterialTheme for preview
        MenuScreen()
    }
}
