package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.system.homescreen.*
import android.os.Bundle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import dev.aurakai.auraframefx.ui.viewmodel.HomeScreenTransitionViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTransitionScreen(
    viewModel: HomeScreenTransitionViewModel = hiltViewModel(),
) {
    val currentConfig by viewModel.currentConfig.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    var currentType by remember { mutableStateOf<HomeScreenTransitionType?>(null) }
    var currentEffect by remember { mutableStateOf<HomeScreenTransitionEffect?>(null) }
    
    // Create a local state holder for the effect type
    val localEffectType = remember { mutableStateOf(currentEffect?.type) }
    var currentDuration by remember { mutableStateOf(300) }
    var currentProperties by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    
    // Update local state when config changes
    LaunchedEffect(currentConfig) {
        currentConfig?.let { config ->
            currentEffect = config.defaultOutgoingEffect
            currentType = currentEffect?.type
            config.duration?.let { duration ->
                currentDuration = duration
            }
            currentProperties = currentEffect?.properties ?: emptyMap()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Screen Transitions") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    // Reset to default values
                    coroutineScope.launch {
                        viewModel.resetToDefault()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset to Default")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transition Type Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00FFCC).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Transition Type",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Outgoing effect type picker
                    Text(
                        text = "Outgoing Effect",
                        style = MaterialTheme.typography.titleSmall
                    )
                    TransitionTypePicker(
                        currentType = currentType ?: HomeScreenTransitionType.GLOBE_ROTATE,
                        onTypeSelected = { type -> 
                            currentType = type
                            val newEffect = currentEffect?.copy(type = type) ?: 
                                HomeScreenTransitionEffect(
                                    type = type, 
                                    properties = emptyMap()
                                )
                            currentEffect = newEffect
                            viewModel.updateTransitionProperties(
                                mapOf("defaultOutgoingEffect" to newEffect as Any)
                            )
                        }
                    )
                    
                    // Incoming effect type picker
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Incoming Effect",
                        style = MaterialTheme.typography.titleSmall
                    )
                    TransitionTypePicker(
                        currentType = currentConfig?.defaultIncomingEffect?.type ?: HomeScreenTransitionType.GLOBE_ROTATE.name,
                        onTypeSelected = { type ->
                            val currentEffect = currentConfig?.defaultIncomingEffect
                            val newEffect = currentEffect?.copy(type = type) ?: HomeScreenTransitionEffect(
                                type = type,
                                properties = TransitionProperties()
                            )
                            viewModel.updateTransitionProperties(
                                mapOf("defaultIncomingEffect" to newEffect as Any)
                            )
                        }
                    )
                }
            }

            // Duration Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00FFCC).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Duration",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Duration slider for outgoing effect
                    currentConfig?.defaultOutgoingEffect?.properties?.let { props ->
                        DurationSlider(
                            currentDuration = props.duration.toInt(),
                            onDurationChanged = { duration ->
                                val updatedProps = props.copy(duration = duration.toLong())
                                viewModel.updateTransitionProperties(
                                    mapOf("defaultOutgoingEffect" to currentConfig.defaultOutgoingEffect?.copy(properties = updatedProps))
                                )
                            }
                        )
                    }
                    
                    // Duration slider for incoming effect
                    currentConfig?.defaultIncomingEffect?.properties?.let { props ->
                        DurationSlider(
                            currentDuration = props.duration.toInt(),
                            onDurationChanged = { duration ->
                                val updatedProps = props.copy(duration = duration.toLong())
                                viewModel.updateTransitionProperties(
                                    mapOf("defaultIncomingEffect" to currentConfig.defaultIncomingEffect?.copy(properties = updatedProps))
                                )
                            }
                        )
                    }
                }
            }

            // Properties Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00FFCC).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Properties",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Current Transition: ${currentConfig?.defaultOutgoingEffect?.type?.name ?: "None"}")
                    Text("Duration: ${currentDuration.value}ms")
                    Spacer(modifier = Modifier.height(8.dp))
                    // Properties editor for outgoing effect
                    currentConfig?.defaultOutgoingEffect?.properties?.let { props ->
                        TransitionPropertiesEditor(
                            currentProperties = mapOf(
                                "direction" to (props.direction ?: ""),
                                "interpolator" to props.interpolator
                            ),
                            onPropertiesChanged = { properties ->
                                val updatedProps = props.copy(
                                    direction = properties["direction"] as? String ?: props.direction,
                                    interpolator = properties["interpolator"] as? String ?: props.interpolator
                                )
                                viewModel.updateTransitionProperties(
                                    mapOf("defaultOutgoingEffect" to currentConfig.defaultOutgoingEffect?.copy(properties = updatedProps))
                                )
                            }
                        )
                    }
                    
                    // Properties editor for incoming effect
                    currentConfig?.defaultIncomingEffect?.properties?.let { props ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Incoming Effect Properties",
                            style = MaterialTheme.typography.titleSmall
                        )
                        TransitionPropertiesEditor(
                            currentProperties = mapOf(
                                "direction" to (props.direction ?: ""),
                                "interpolator" to props.interpolator
                            ),
                            onPropertiesChanged = { properties ->
                                val updatedProps = props.copy(
                                    direction = properties["direction"] as? String ?: props.direction,
                                    interpolator = properties["interpolator"] as? String ?: props.interpolator
                                )
                                viewModel.updateTransitionProperties(
                                    mapOf("defaultIncomingEffect" to currentConfig.defaultIncomingEffect?.copy(properties = updatedProps))
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransitionTypePicker(
    currentType: HomeScreenTransitionType,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
): Unit {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Basic Transitions
        Text(
            text = "Basic Transitions",
            style = MaterialTheme.typography.titleSmall
        )
        BasicTransitionRow(
            currentType = currentType,
            onTypeSelected = onTypeSelected
        )

        // Card Stack Transitions
        Text(
            text = "Card Stack Transitions",
            style = MaterialTheme.typography.titleSmall
        )
        CardStackTransitionRow(
            currentType = currentType,
            onTypeSelected = onTypeSelected
        )

        // 3D Transitions
        Text(
            text = "3D Transitions",
            style = MaterialTheme.typography.titleSmall
        )
        ThreeDTransitionRow(
            currentType = currentType,
            onTypeSelected = onTypeSelected
        )

        // Globe Transitions
        Text(
            text = "Globe Transitions",
            style = MaterialTheme.typography.titleSmall
        )
        GlobeTransitionRow(
            currentType = currentType,
            onTypeSelected = onTypeSelected
        )

        // Fan Transitions
        Text(
            text = "Fan Transitions",
            style = MaterialTheme.typography.titleSmall
        )
        FanTransitionRow(
            currentType = currentType,
            onTypeSelected = onTypeSelected
        )

        // Spread Transitions
        Text(
            text = "Spread Transitions",
            style = MaterialTheme.typography.titleSmall
        )
        SpreadTransitionRow(
            currentType = currentType,
            onTypeSelected = onTypeSelected
        )
        // Digital/Hologram Transitions
        Text(
            text = "Digital/Hologram Transitions",
            style = MaterialTheme.typography.titleSmall
        )
        DigitalHologramTransitionRow(
            currentType = currentType,
            onTypeSelected = onTypeSelected
        )
    }
}

@Composable
fun DurationSlider(
    currentDuration: Int,
    onDurationChanged: (Int) -> Unit,
): Unit {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Duration (ms)",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = currentDuration.toFloat(),
            onValueChange = { onDurationChanged(it.toInt()) },
            valueRange = 100f..2000f,
            steps = 19,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${currentDuration}ms",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun TransitionPropertiesEditor(
    currentProperties: Map<String, Any>,
    onPropertiesChanged: (Map<String, Any>) -> Unit,
): Unit {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Angle
        PropertySlider(
            label = "Angle",
            value = currentProperties["angle"] as? Float ?: 0f,
            onValueChange = { angle ->
                onPropertiesChanged(currentProperties + ("angle" to angle))
            },
            valueRange = 0f..360f
        )

        // Scale
        PropertySlider(
            label = "Scale",
            value = currentProperties["scale"] as? Float ?: 1f,
            onValueChange = { scale ->
                onPropertiesChanged(currentProperties + ("scale" to scale))
            },
            valueRange = 0.1f..2f
        )

        // Offset
        PropertySlider(
            label = "Offset",
            value = currentProperties["offset"] as? Float ?: 0f,
            onValueChange = { offset ->
                onPropertiesChanged(currentProperties + ("offset" to offset))
            },
            valueRange = -100f..100f
        )

        // Amplitude
        PropertySlider(
            label = "Amplitude",
            value = currentProperties["amplitude"] as? Float ?: 0f,
            onValueChange = { amplitude ->
                onPropertiesChanged(currentProperties + ("amplitude" to amplitude))
            },
            valueRange = 0f..1f
        )

        // Frequency
        PropertySlider(
            label = "Frequency",
            value = currentProperties["frequency"] as? Float ?: 0f,
            onValueChange = { frequency ->
                onPropertiesChanged(currentProperties + ("frequency" to frequency))
            },
            valueRange = 0f..2f
        )

        // Blur
        PropertySlider(
            label = "Blur",
            value = currentProperties["blur"] as? Float ?: 0f,
            onValueChange = { blur ->
                onPropertiesChanged(currentProperties + ("blur" to blur))
            },
            valueRange = 0f..100f
        )

        // Spread
        PropertySlider(
            label = "Spread",
            value = currentProperties["spread"] as? Float ?: 0f,
            onValueChange = { spread ->
                onPropertiesChanged(currentProperties + ("spread" to spread))
            },
            valueRange = 0f..1f
        )
    }
}

@Composable
fun PropertySlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = String.format(Locale.ROOT, "%.2f", value),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BasicTransitionRow(
    currentType: HomeScreenTransitionType?,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Basic Transitions",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TransitionButton(
                label = "Slide Left",
                isSelected = currentType == HomeScreenTransitionType.SLIDE_LEFT,
                onClick = { onTypeSelected(HomeScreenTransitionType.SLIDE_LEFT) }
            )
            TransitionButton(
                label = "Slide Right",
                isSelected = currentType == HomeScreenTransitionType.SLIDE_RIGHT,
                onClick = { onTypeSelected(HomeScreenTransitionType.SLIDE_RIGHT) }
            )
            TransitionButton(
                label = "Fade",
                isSelected = currentType == HomeScreenTransitionType.FADE,
                onClick = { onTypeSelected(HomeScreenTransitionType.FADE) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TransitionButton(
                label = "Slide Up",
                isSelected = currentType == HomeScreenTransitionType.SLIDE_UP,
                onClick = { onTypeSelected(HomeScreenTransitionType.SLIDE_UP) }
            )
            TransitionButton(
                label = "Slide Down",
                isSelected = currentType == HomeScreenTransitionType.SLIDE_DOWN,
                onClick = { onTypeSelected(HomeScreenTransitionType.SLIDE_DOWN) }
            )
        }
    }
}

@Composable
fun CardStackTransitionRow(
    currentType: HomeScreenTransitionType?,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TransitionButton(
            label = "Stack Slide",
            isSelected = currentType == HomeScreenTransitionType.STACK_SLIDE,
            onClick = { onTypeSelected(HomeScreenTransitionType.STACK_SLIDE) }
        )
        TransitionButton(
            label = "Stack Fade",
            isSelected = currentType == HomeScreenTransitionType.STACK_FADE,
            onClick = { onTypeSelected(HomeScreenTransitionType.STACK_FADE) }
        )
        TransitionButton(
            label = "Stack Scale",
            isSelected = currentType == HomeScreenTransitionType.STACK_SCALE,
            onClick = { onTypeSelected(HomeScreenTransitionType.STACK_SCALE) }
        )
        TransitionButton(
            label = "Stack Rotate",
            isSelected = currentType == HomeScreenTransitionType.STACK_ROTATE,
            onClick = { onTypeSelected(HomeScreenTransitionType.STACK_ROTATE) }
        )
    }
}

@Composable
fun ThreeDTransitionRow(
    currentType: HomeScreenTransitionType?,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TransitionButton(
            label = "3D Rotate",
            isSelected = currentType == HomeScreenTransitionType.STACK_ROTATE_3D,
            onClick = { onTypeSelected(HomeScreenTransitionType.STACK_ROTATE_3D) }
        )
        TransitionButton(
            label = "3D Scale",
            isSelected = currentType == HomeScreenTransitionType.STACK_SCALE_3D,
            onClick = { onTypeSelected(HomeScreenTransitionType.STACK_SCALE_3D) }
        )
        TransitionButton(
            label = "3D Slide",
            isSelected = currentType == HomeScreenTransitionType.STACK_SLIDE_3D,
            onClick = { onTypeSelected(HomeScreenTransitionType.STACK_SLIDE_3D) }
        )
        TransitionButton(
            label = "3D Wave",
            isSelected = currentType == HomeScreenTransitionType.STACK_WAVE_3D,
            onClick = { onTypeSelected(HomeScreenTransitionType.STACK_WAVE_3D) }
        )
    }
}

@Composable
fun GlobeTransitionRow(
    currentType: HomeScreenTransitionType,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TransitionButton(
            label = "Globe Rotate",
            isSelected = currentType == HomeScreenTransitionType.GLOBE_ROTATE,
            onClick = { onTypeSelected(HomeScreenTransitionType.GLOBE_ROTATE) }
        )
        TransitionButton(
            label = "Globe Scale",
            isSelected = currentType == HomeScreenTransitionType.GLOBE_SCALE,
            onClick = { onTypeSelected(HomeScreenTransitionType.GLOBE_SCALE) }
        )
        TransitionButton(
            label = "Globe Pulse",
            isSelected = currentType == HomeScreenTransitionType.GLOBE_PULSE,
            onClick = { onTypeSelected(HomeScreenTransitionType.GLOBE_PULSE) }
        )
        TransitionButton(
            label = "Globe Glow",
            isSelected = currentType == HomeScreenTransitionType.GLOBE_GLOW,
            onClick = { onTypeSelected(HomeScreenTransitionType.GLOBE_GLOW) }
        )
    }
}

@Composable
fun FanTransitionRow(
    currentType: HomeScreenTransitionType,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TransitionButton(
            label = "Fan In",
            isSelected = currentType == HomeScreenTransitionType.FAN_IN,
            onClick = { onTypeSelected(HomeScreenTransitionType.FAN_IN) }
        )
        TransitionButton(
            label = "Fan Out",
            isSelected = currentType == HomeScreenTransitionType.FAN_OUT,
            onClick = { onTypeSelected(HomeScreenTransitionType.FAN_OUT) }
        )
        TransitionButton(
            label = "Fan Rotate",
            isSelected = currentType == HomeScreenTransitionType.FAN_ROTATE,
            onClick = { onTypeSelected(HomeScreenTransitionType.FAN_ROTATE) }
        )
        TransitionButton(
            label = "Fan Scale",
            isSelected = currentType == HomeScreenTransitionType.FAN_SCALE,
            onClick = { onTypeSelected(HomeScreenTransitionType.FAN_SCALE) }
        )
    }
}

@Composable
fun SpreadTransitionRow(
    currentType: HomeScreenTransitionType,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TransitionButton(
            label = "Spread In",
            isSelected = currentType == HomeScreenTransitionType.SPREAD_IN,
            onClick = { onTypeSelected(HomeScreenTransitionType.SPREAD_IN) }
        )
        TransitionButton(
            label = "Spread Out",
            isSelected = currentType == HomeScreenTransitionType.SPREAD_OUT,
            onClick = { onTypeSelected(HomeScreenTransitionType.SPREAD_OUT) }
        )
        TransitionButton(
            label = "Spread Rotate",
            isSelected = currentType == HomeScreenTransitionType.SPREAD_ROTATE,
            onClick = { onTypeSelected(HomeScreenTransitionType.SPREAD_ROTATE) }
        )
        TransitionButton(
            label = "Spread Scale",
            isSelected = currentType == HomeScreenTransitionType.SPREAD_SCALE,
            onClick = { onTypeSelected(HomeScreenTransitionType.SPREAD_SCALE) }
        )
    }
}

@Composable
fun DigitalHologramTransitionRow(
    currentType: HomeScreenTransitionType,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
): Unit {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Digital Deconstruct
        TransitionButton(
            label = "Digital Deconstruct",
            isSelected = currentType == HomeScreenTransitionType.DIGITAL_DECONSTRUCT,
            onClick = { onTypeSelected(HomeScreenTransitionType.DIGITAL_DECONSTRUCT) }
        )

        // Digital Reconstruct
        TransitionButton(
            label = "Digital Reconstruct",
            isSelected = currentType == HomeScreenTransitionType.DIGITAL_RECONSTRUCT,
            onClick = { onTypeSelected(HomeScreenTransitionType.DIGITAL_RECONSTRUCT) }
        )
    }
}

@Composable
fun TransitionButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = if (isSelected) 2.dp else 1.dp
        ),
        modifier = Modifier.width(120.dp)
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}
