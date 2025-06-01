package dev.aurakai.auraframefx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.theme.Pink80
import dev.aurakai.auraframefx.ui.theme.Purple80

@Composable
fun MenuScreen(onMenuSelected: (String) -> Unit, onPlayground: () -> Unit = {}) {
    val menuItems = listOf(
        "UI ENGINE",
        "AuraShield",
        "AurakaiEcoSys",
        "Conference Room",
        "Xhancement"
    )
    val footerItems = listOf(
        "Community",
        "Help Desk"
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "AuraFrameFX",
            fontSize = 32.sp,
            color = Pink80,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            "Aura's Creativity Engine",
            fontSize = 18.sp,
            color = Purple80,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        menuItems.forEach { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color(0xFF000000), RoundedCornerShape(16.dp))
                    .border(2.dp, Pink80, RoundedCornerShape(16.dp))
                    .clickable { onMenuSelected(item) }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(item, fontSize = 22.sp, color = Pink80)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Z-Order Playground button
        Button(
            onClick = onPlayground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                "Cascade Z-Order Playground",
                fontSize = 18.sp,
                color = Pink80 // Use your neon accent color
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            footerItems.forEach { item ->
                Text(
                    item,
                    fontSize = 16.sp,
                    color = Purple80,
                    modifier = Modifier
                        .clickable { onMenuSelected(item) }
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}
