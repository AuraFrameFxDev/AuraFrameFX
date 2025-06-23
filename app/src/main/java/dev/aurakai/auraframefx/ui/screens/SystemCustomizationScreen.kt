package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectAsState
import kotlinx.coroutines.launch
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsConfig
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsTileConfig
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsAnimation
import dev.aurakai.auraframefx.system.lockscreen.LockScreenConfig
import dev.aurakai.auraframefx.system.lockscreen.LockScreenElementConfig
import dev.aurakai.auraframefx.system.lockscreen.LockScreenBackgroundConfig
import dev.aurakai.auraframefx.system.lockscreen.LockScreenAnimation
import dev.aurakai.auraframefx.system.overlay.OverlayShape
import dev.aurakai.auraframefx.system.overlay.OverlayImage
import dev.aurakai.auraframefx.ui.viewmodel.SystemCustomizationViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectAsState

// Type alias for image resources
typealias ImageResource = Any

// Data class for UI state
// Data class representing a lock screen element in the UI
data class LockScreenElement(
    val elementId: String,
    val isVisible: Boolean = true,
    val customText: String? = null,
    val animation: LockScreenAnimation = LockScreenAnimation()
) {
    // Create a copy with updated visibility
    fun withVisibility(visible: Boolean): LockScreenElement {
        return copy(isVisible = visible)
    }
    
    // Create a copy with updated custom text
    fun withCustomText(text: String?): LockScreenElement {
        return copy(customText = text)
    }
}

// Extension to convert LockScreenElement to LockScreenElementConfig
private fun LockScreenElement.toConfig(): LockScreenElementConfig {
    return LockScreenElementConfig(
        elementId = this.elementId,
        isVisible = this.isVisible,
        customText = this.customText
    )
}

// Extension properties for null safety
private val QuickSettingsConfig?.safeTiles: List<QuickSettingsTileConfig>
    get() = this?.tiles ?: emptyList()

private val LockScreenConfig?.safeElements: List<LockScreenElementConfig>
    get() = this?.elements ?: emptyList()

// Helper function to get background from config
private fun LockScreenConfig?.safeBackground(): String? {
    return this?.backgroundConfig?.source
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemCustomizationScreen(
    viewModel: SystemCustomizationViewModel = hiltViewModel(),
) {
    // Collect state from ViewModel
    val quickSettingsConfig by viewModel.quickSettingsConfig.collectAsState(initial = null)
    val lockScreenConfig by viewModel.lockScreenConfig.collectAsState(initial = null)
    
    // The ViewModel handles loading in its init block
    // We just need to ensure we're collecting the flows
    LaunchedEffect(Unit) {
        // No-op - just need the LaunchedEffect to trigger flow collection
    }
    
    // Handle side effects
    LaunchedEffect(Unit) {
        // Initial data load or side effects can go here
        viewModel.loadConfigurations()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Customization") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Navigate back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.resetToDefaults() }
            ) {
                Icon(Icons.Default.Restore, "Reset")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Settings Section
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
                        text = "Quick Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickSettingsCustomization(
                        config = quickSettingsConfig,
                        onTileShapeChange = { tileId, shape ->
                            viewModel.updateQuickSettingsTileShape(tileId, shape)
                        },
                        onTileAnimationChange = { tileId, animation ->
                            viewModel.updateQuickSettingsTileAnimation(tileId, animation)
                        },
                        onBackgroundChange = { image ->
                            viewModel.updateQuickSettingsBackground(image)
                        }
                    )
                }
            }

            // Lock Screen Section
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
                        text = "Lock Screen",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LockScreenCustomization(
                        config = lockScreenConfig,
                        onElementChange = { element ->
                            viewModel.updateLockScreenElement(element.toConfig())
                        },
                        onBackgroundChange = { image ->
                            viewModel.updateLockScreenBackground(image)
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun QuickSettingsCustomization(
    config: QuickSettingsConfig?,
    onTileShapeChange: (String, OverlayShape) -> Unit = { _, _ -> },
    onTileAnimationChange: (String, QuickSettingsAnimation) -> Unit = { _, _ -> },
    onBackgroundChange: (ImageResource?) -> Unit = {}
) {
    config?.let { current ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tiles Section
            Text(
                text = "Tiles",
                style = MaterialTheme.typography.titleSmall
            )
            // Safe call on tiles since it's nullable
            current.safeTiles.forEach { tile ->
                TileCustomization(
                    tile = tile,
                    onShapeChange = { shape -> onTileShapeChange(tile.tileId, shape) },
                    onAnimationChange = { animation -> onTileAnimationChange(tile.tileId, animation) }
                )
            }

            // Background Section
            Text(
                text = "Background",
                style = MaterialTheme.typography.titleSmall
            )
            // Only show background customization if background is available
            val background = current.safeBackground()
            BackgroundCustomization(
                background = background,
                onChange = { newBackground ->
                    // Update the background in the config
                    onBackgroundChange(newBackground)
                }
            )
        }
    }
}

@Composable
internal fun LockScreenCustomization(
    config: LockScreenConfig?,
    onElementChange: (LockScreenElement) -> Unit = { _ -> },
    onBackgroundChange: (ImageResource?) -> Unit = {}
) {
    // Get the ViewModel
    val viewModel: SystemCustomizationViewModel = hiltViewModel()
    // Create sample elements for preview/demo
    val sampleElements = remember {
        listOf(
            LockScreenElement(
                elementId = "sample",
                isVisible = true,
                customText = "Sample",
                animation = LockScreenAnimation()
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Elements Section
        Text(
            text = "Elements",
            style = MaterialTheme.typography.titleSmall
        )
        
        // Use sample elements if config is null for preview
        val elements = if (config != null) {
            config.safeElements.map { element ->
                LockScreenElement(
                    elementId = element.elementId,
                    isVisible = element.isVisible,
                    customText = element.customText,
                    animation = LockScreenAnimation()
                )
            }
        } else {
            sampleElements
        }
        
        elements.forEach { element ->
            // Create a LockScreenElementType from the elementId
            val elementType = object : LockScreenElementType {
                override val typeId: String = element.elementId
            }
            
            // Update element visibility and text
            LaunchedEffect(element) {
                viewModel.updateLockScreenElementShape(
                    elementType = elementType,
                    shape = OverlayShape() // Default shape
                )
                viewModel.updateLockScreenElementAnimation(
                    elementType = elementType,
                    animation = element.animation
                )
            }
            
            ElementCustomization(
                element = element,
                onElementChange = { newElement -> 
                    // Update element in the ViewModel
                    val newElementType = object : LockScreenElementType {
                        override val typeId: String = newElement.elementId
                    }
                    viewModel.updateLockScreenElementShape(
                        elementType = newElementType,
                        shape = OverlayShape() // Default shape
                    )
                    viewModel.updateLockScreenElementAnimation(
                        elementType = newElementType,
                        animation = newElement.animation
                    )
                }
            )
        }

        // Background customization
        Text(
            text = "Background",
            style = MaterialTheme.typography.titleSmall
        )
        // Only show background customization if we have a source
        config?.backgroundConfig?.source?.let { source ->
            BackgroundCustomization(
                background = source as? ImageResource,
                onChange = onBackgroundChange
            )
        }
    }
}

// Helper class for LockScreen background
private class LockScreenBackground(val image: Any?)

@Composable
private fun TileCustomization(
    tile: QuickSettingsTileConfig,
    onShapeChange: (OverlayShape) -> Unit = {},
    onAnimationChange: (QuickSettingsAnimation) -> Unit = {}
) {
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
                text = tile.label ?: "Tile",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Shape Picker
            Text(
                text = "Shape",
                style = MaterialTheme.typography.bodyMedium
            )
            ShapePicker(
                currentShape = tile.shape,
                onShapeSelected = onShapeChange
            )

            // Animation Picker
            Text(
                text = "Animation",
                style = MaterialTheme.typography.bodyMedium
            )
            AnimationPicker(
                currentAnimation = tile.animation,
                onAnimationSelected = onAnimationChange
            )
        }
    }
}

@Composable
private fun ElementCustomization(
    element: LockScreenElement,
    onElementChange: (LockScreenElement) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFCCE5FF).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Element: ${element.elementId}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Toggle visibility
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Visible")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = element.isVisible,
                    onCheckedChange = { isChecked ->
                        onElementChange(element.withVisibility(isChecked))
                    }
                )
            }
            
            // Custom text input
            if (element.isVisible) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = element.customText ?: "",
                    onValueChange = { text ->
                        onElementChange(element.withCustomText(text.ifEmpty { null }))
                    },
                    label = { Text("Custom Text") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun BackgroundCustomization(
    background: ImageResource?,
    onChange: (ImageResource?) -> Unit = {}
) {
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
                text = "Background Image",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Image Picker
            ImagePicker(
                currentImage = background,
                onImageSelected = onChange
            )
        }
    }
}

@Composable
private fun ShapePicker(
    currentShape: OverlayShape,
    onShapeSelected: (OverlayShape) -> Unit = {}
) {
    // Use the callback
    LaunchedEffect(Unit) {
        onShapeSelected(currentShape)
    }
    // Simple placeholder UI for shape selection
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Shape",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Add shape selection UI here
        // For now, just show the current shape name
        Text(
            text = "Selected: ${currentShape::class.simpleName ?: "None"}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun AnimationPicker(
    currentAnimation: QuickSettingsAnimation,
    onAnimationSelected: (QuickSettingsAnimation) -> Unit = {}
) {
    // Use the callback
    LaunchedEffect(Unit) {
        onAnimationSelected(currentAnimation)
    }
    // Simple placeholder UI for animation selection
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Animation",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Add animation selection UI here
        // For now, just show the current animation name
        Text(
            text = "Selected: ${currentAnimation::class.simpleName ?: "None"}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ImagePicker(
    currentImage: Any?,
    onImageSelected: (Any?) -> Unit = {}
) {
    // Use the callback
    LaunchedEffect(Unit) {
        onImageSelected(currentImage)
    }
    // Simple placeholder UI for image selection
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Background Image",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Add image selection UI here
        Text(
            text = if (currentImage != null) "Image Selected" else "No Image Selected",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
