package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionType
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonPink

/**
 * A composable that displays a horizontal row of transition type options
 * @param currentType The currently selected transition type
 * @param onTypeSelected Callback when a transition type is selected
 */
@Composable
fun DigitalTransitionRow(
    currentType: HomeScreenTransitionType,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = "Transition Style",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(HomeScreenTransitionType.values()) { type ->
                val isSelected = type == currentType

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) NeonBlue.copy(alpha = 0.2f)
                            else Color.Transparent
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) NeonPink else Color.Gray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onTypeSelected(type) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = type.name.replace("_", " "),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) NeonPink else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
