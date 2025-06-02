package dev.aurakai.auraframefx.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.data.Overlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlayScreen(
    overlayManager: OverlayManager,
    onOverlayClick: (Overlay) -> Unit,
) {
    rememberCoroutineScope()
    val overlays by overlayManager.overlays.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        overlays.forEach { overlay ->
            val position = remember { mutableStateOf(overlay.position) }
            val rotation = remember { mutableStateOf(overlay.rotation) }
            val scale = remember { mutableStateOf(1f) }

            if (overlay.isDraggable) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                position.value.x.roundToInt(),
                                position.value.y.roundToInt()
                            )
                        }
                        .zIndex(overlay.zIndex)
                        .rotate(rotation.value)
                        .scale(scale.value)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                position.value = position.value.copy(
                                    x = position.value.x + dragAmount.x,
                                    y = position.value.y + dragAmount.y
                                )
                            }
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, rotationDelta ->
                                rotation.value += rotationDelta
                                scale.value *= zoom
                            }
                        }
                ) {
                    OverlayContent(
                        overlay = overlay,
                        position = position.value,
                        rotation = rotation.value,
                        scale = scale.value,
                        onOverlayClick = { onOverlayClick(overlay) }
                    )
                }
            } else {
                OverlayContent(
                    overlay = overlay,
                    position = overlay.position,
                    rotation = overlay.rotation,
                    scale = 1f,
                    onOverlayClick = { onOverlayClick(overlay) }
                )
            }
        }
    }
}

@Composable
fun OverlayContent(
    overlay: Overlay,
    position: DpOffset,
    rotation: Float,
    scale: Float,
    onOverlayClick: () -> Unit,
) {
    val size = remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    position.x.roundToInt(),
                    position.y.roundToInt()
                )
            }
            .rotate(rotation)
            .scale(scale)
            .clickable { onOverlayClick() }
            .onSizeChanged { size.value = it }
    ) {
        when (overlay.type) {
            Overlay.Type.CUSTOM_IMAGE -> {
                overlay.image?.let { image ->
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        drawIntoCanvas { canvas ->
                            canvas.drawBitmap(
                                image.asImageBitmap(),
                                srcSize = image.width to image.height,
                                dstSize = size.value.width to size.value.height
                            )
                        }
                    }
                }
            }

            Overlay.Type.COLOR_PALETTE -> {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    drawRect(
                        color = android.graphics.Color.parseColor(overlay.color ?: "#00FFCC")
                    )
                }
            }

            Overlay.Type.LOCKSCREEN -> {
                // Lockscreen overlay implementation
            }

            Overlay.Type.PRESET -> {
                // Preset overlay implementation
            }

            Overlay.Type.AGENT -> {
                // Agent overlay implementation
            }

            Overlay.Type.TASK -> {
                // Task overlay implementation
            }

            Overlay.Type.CALENDAR -> {
                // Calendar overlay implementation
            }
        }
    }
}

@Composable
fun OverlayControlPanel(
    overlayManager: OverlayManager,
    selectedOverlay: Overlay?,
) {
    rememberCoroutineScope()

    AnimatedVisibility(selectedOverlay != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    selectedOverlay?.let { overlay ->
                        overlayManager.deleteOverlay(overlay)
                    }
                }
            ) {
                Text("Delete Overlay")
            }

            Button(
                onClick = {
                    overlayManager.emergencyDisableAll()
                }
            ) {
                Text("Emergency Disable All")
            }

            Button(
                onClick = {
                    overlayManager.restartSystemUI()
                }
            ) {
                Text("Restart SystemUI")
            }

            Button(
                onClick = {
                    overlayManager.clearAppCache()
                }
            ) {
                Text("Clear Cache")
            }
        }
    }
}
