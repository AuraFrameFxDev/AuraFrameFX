package dev.aurakai.auraframefx.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.aurakai.auraframefx.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CascadeZOrderPlayground() {
    LocalContext.current
    rememberCoroutineScope()

    // Background image selection
    var bgImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            bgImageUri = uri
        }

    // Preset images (add your own drawables here)
    val presetImages: List<Int> = listOf(
        R.drawable.orb_static, // Example static drawable
        R.mipmap.aura // Example PNG from mipmap
        // Add more drawables or mipmaps here as needed
    )
    var selectedPreset by remember { mutableStateOf<Int?>(null) }

    // Widget positions (battery, clock, etc)
    var batteryAlignment by remember { mutableStateOf(Alignment.BottomEnd) }
    var clockAlignment by remember { mutableStateOf(Alignment.TopStart) }
    var showBattery by remember { mutableStateOf(true) }
    var showClock by remember { mutableStateOf(true) }
    var showMusic by remember { mutableStateOf(false) }
    var showWeather by remember { mutableStateOf(false) }
    var showNotification by remember { mutableStateOf(false) }
    var showCustomText by remember { mutableStateOf(false) }
    var customText by remember { mutableStateOf("Hello, AuraFrameFX!") }

    // Drag state for overlays
    var batteryOffset by remember { mutableStateOf<Offset>(Offset.Zero) }
    var clockOffset by remember { mutableStateOf<Offset>(Offset.Zero) }
    var musicOffset by remember { mutableStateOf<Offset>(Offset.Zero) }
    var weatherOffset by remember { mutableStateOf<Offset>(Offset.Zero) }
    var notificationOffset by remember { mutableStateOf<Offset>(Offset.Zero) }
    var customTextOffset by remember { mutableStateOf<Offset>(Offset.Zero) }

    // Color palette state
    val presetColors = listOf(
        Color(0xFF00FFEA), // Neon teal
        Color(0xFFB388FF), // Neon purple
        Color(0xFFFF4081), // Neon pink
        Color(0xFF00C853), // Neon green
        Color(0xFF2196F3), // Neon blue
        Color(0xFF9C27B0), // Deep purple
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFFFFFF), // White
        Color(0xFF000000)  // Black
    )
    var overlayColor by remember { mutableStateOf(presetColors[0]) }
    var showColorDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        // Background image (user or preset)
        when {
            bgImageUri != null -> {
                AsyncImage(
                    model = bgImageUri,
                    contentDescription = "Custom Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            selectedPreset != null -> {
                Image(
                    painter = painterResource(id = selectedPreset!!),
                    contentDescription = "Preset Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                // Default background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }
        }

        // Playground menu
        Column(
            Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xCC22223A))
                .padding(12.dp)
        ) {
            Text("Cascade Z-Order Playground", color = Color.Cyan, fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { pickImageLauncher.launch("image/*") }) {
                    Text("Pick Image")
                }
                Spacer(Modifier.width(8.dp))
                presetImages.forEachIndexed { idx, resId ->
                    Button(onClick = { selectedPreset = resId }) {
                        Text("Preset ${idx + 1}")
                    }
                    Spacer(Modifier.width(4.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Overlay Color Palette", color = Color.Cyan, fontSize = 16.sp)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                presetColors.forEach { color ->
                    Box(
                        Modifier
                            .padding(4.dp)
                            .size(32.dp)
                            .background(color, RoundedCornerShape(50))
                            .border(
                                2.dp,
                                if (overlayColor == color) Color.White else Color.Transparent,
                                RoundedCornerShape(50)
                            )
                            .clickable { overlayColor = color }
                    )
                }
                Button(
                    onClick = { showColorDialog = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Custom")
                }
            }
            if (showColorDialog) {
                AlertDialog(
                    onDismissRequest = { showColorDialog = false },
                    title = { Text("Pick Custom Color") },
                    text = {
                        // Simple color picker: just show a few more options for now
                        Row {
                            listOf(
                                Color.Red,
                                Color.Green,
                                Color.Blue,
                                Color.Yellow,
                                Color.Magenta,
                                Color.Cyan,
                                Color.Gray
                            ).forEach { color ->
                                Box(
                                    Modifier
                                        .padding(4.dp)
                                        .size(32.dp)
                                        .background(color, RoundedCornerShape(50))
                                        .clickable {
                                            overlayColor = color
                                            showColorDialog = false
                                        }
                                )
                            }
                        }
                        Text("(Advanced color picker coming soon)")
                    },
                    confirmButton = {
                        Button(onClick = { showColorDialog = false }) { Text("Close") }
                    }
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showBattery, onCheckedChange = { showBattery = it })
                Text("Show Battery", color = Color.White)
                Spacer(Modifier.width(12.dp))
                Checkbox(checked = showClock, onCheckedChange = { showClock = it })
                Text("Show Clock", color = Color.White)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showMusic, onCheckedChange = { showMusic = it })
                Text("Show Music Player", color = Color.White)
                Spacer(Modifier.width(12.dp))
                Checkbox(checked = showWeather, onCheckedChange = { showWeather = it })
                Text("Show Weather", color = Color.White)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showNotification, onCheckedChange = { showNotification = it })
                Text("Show Notification", color = Color.White)
                Spacer(Modifier.width(12.dp))
                Checkbox(checked = showCustomText, onCheckedChange = { showCustomText = it })
                Text("Show Custom Text", color = Color.White)
            }
            if (showCustomText) {
                OutlinedTextField(
                    value = customText,
                    onValueChange = { customText = it },
                    label = { Text("Custom Text") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Battery overlay (draggable, z-order above bg)
        if (showBattery) {
            Box(
                Modifier
                    .align(batteryAlignment)
                    .offset { IntOffset(batteryOffset.x.toInt(), batteryOffset.y.toInt()) }
                    .shadow(8.dp, RoundedCornerShape(50))
                    .background(overlayColor.copy(alpha = 0.7f), RoundedCornerShape(50))
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            batteryOffset += dragAmount
                        }
                    }
            ) {
                Text("🔋 87%", color = Color.Black, fontSize = 18.sp)
            }
        }
        // Clock overlay (draggable)
        if (showClock) {
            Box(
                Modifier
                    .align(clockAlignment)
                    .offset { IntOffset(clockOffset.x.toInt(), clockOffset.y.toInt()) }
                    .shadow(8.dp, RoundedCornerShape(50))
                    .background(overlayColor.copy(alpha = 0.7f), RoundedCornerShape(50))
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            clockOffset += dragAmount
                        }
                    }
            ) {
                Text("🕒 12:42", color = Color.Black, fontSize = 18.sp)
            }
        }
        // Music Player overlay
        if (showMusic) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .offset { IntOffset(musicOffset.x.toInt(), musicOffset.y.toInt()) }
                    .shadow(8.dp, RoundedCornerShape(32))
                    .background(overlayColor.copy(alpha = 0.8f), RoundedCornerShape(32))
                    .padding(horizontal = 22.dp, vertical = 12.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            musicOffset += dragAmount
                        }
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎵", fontSize = 22.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Now Playing: Neon Drive", color = Color.White, fontSize = 16.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("⏸️", fontSize = 22.sp)
                }
            }
        }
        // Weather overlay
        if (showWeather) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset { IntOffset(weatherOffset.x.toInt(), weatherOffset.y.toInt()) }
                    .shadow(8.dp, RoundedCornerShape(32))
                    .background(overlayColor.copy(alpha = 0.8f), RoundedCornerShape(32))
                    .padding(horizontal = 22.dp, vertical = 12.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            weatherOffset += dragAmount
                        }
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("☀️", fontSize = 22.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("72°F Sunny", color = Color.White, fontSize = 16.sp)
                }
            }
        }
        // Notification overlay
        if (showNotification) {
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .offset {
                        IntOffset(
                            notificationOffset.x.toInt(),
                            notificationOffset.y.toInt()
                        )
                    }
                    .shadow(8.dp, RoundedCornerShape(16))
                    .background(overlayColor.copy(alpha = 0.85f), RoundedCornerShape(16))
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            notificationOffset += dragAmount
                        }
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔔", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("New message from Aura!", color = Color.White, fontSize = 15.sp)
                }
            }
        }
        // Custom Text overlay
        if (showCustomText) {
            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .offset { IntOffset(customTextOffset.x.toInt(), customTextOffset.y.toInt()) }
                    .shadow(8.dp, RoundedCornerShape(16))
                    .background(overlayColor.copy(alpha = 0.8f), RoundedCornerShape(16))
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            customTextOffset += dragAmount
                        }
                    }
            ) {
                Text(customText, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
