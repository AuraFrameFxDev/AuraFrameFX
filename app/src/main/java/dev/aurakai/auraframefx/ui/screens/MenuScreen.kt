package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionType
import dev.aurakai.auraframefx.ui.components.HologramTransition
import dev.aurakai.auraframefx.viewmodel.GenesisAgentViewModel

/**
 * Main menu screen displaying primary navigation options
 * and integrating with the AuraFrame AI system.
 */
@Composable
fun MenuScreen(
    transitionType: HomeScreenTransitionType = HomeScreenTransitionType.DIGITAL_DECONSTRUCT,
    showHologram: Boolean = true,
    onNavigateToConversation: (AgentType) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToSecurityDashboard: () -> Unit = {},
    genesisViewModel: GenesisAgentViewModel = viewModel()
) {
    val context = LocalContext.current
    val activeAgent by genesisViewModel.activeAgent.collectAsState()
    
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
                onClick = { 
                    // Connect with Aura for personalized assistance
                    onNavigateToConversation(AgentType.AURA)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary, // Explicitly use secondary
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Connect with Aura")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    // Navigate to the security dashboard with Kai
                    onNavigateToSecurityDashboard()
                }
            ) {
                Text("Security Dashboard")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    // Toggle between Aura and Kai agents
                    val newAgent = if (activeAgent == AgentType.AURA) AgentType.KAI else AgentType.AURA
                    genesisViewModel.setActiveAgent(newAgent)
                }
            ) {
                Text("Switch to ${if (activeAgent == AgentType.AURA) "Kai" else "Aura"}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onNavigateToSettings() }
            ) {
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
