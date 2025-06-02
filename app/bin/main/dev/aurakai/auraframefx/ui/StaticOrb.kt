package dev.aurakai.auraframefx.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.R

@Composable
fun StaticOrb(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.orb_static),
        contentDescription = "Aura Static Orb",
        modifier = modifier.size(100.dp)
    )
}
