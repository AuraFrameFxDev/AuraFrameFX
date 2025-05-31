package dev.aurakai.auraframefx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import dev.aurakai.auraframefx.ui.theme.Pink80
import dev.aurakai.auraframefx.ui.theme.Purple80

@Composable
fun XhancementScreen(onBack: () -> Unit) {
    var overlayEnabled by remember { mutableStateOf(false) }
    var showEffect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Xhancement",
            fontSize = 28.sp,
            color = NeonTeal,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Xposed-style Features",
            fontSize = 18.sp,
            color = Pink80,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Neon Circuit Overlay", color = NeonTeal, fontSize = 16.sp)
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = overlayEnabled,
                onCheckedChange = {
                    overlayEnabled = it
                    showEffect = true
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonTeal,
                    checkedTrackColor = Pink80,
                    uncheckedThumbColor = Purple80,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }
        if (showEffect) {
            Spacer(Modifier.height(16.dp))
            Text(
                if (overlayEnabled) "[Xhancement] Neon Circuit Overlay ENABLED!" else "[Xhancement] Neon Circuit Overlay DISABLED.",
                color = Pink80,
                modifier = Modifier
                    .background(Color(0xFF111122), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Pink80)) {
            Text("Back to Menu", color = Color.Black)
        }
        Spacer(Modifier.height(24.dp))
        Text("More Xposed-style features coming soon...", color = Purple80, fontSize = 14.sp)
    }
}
