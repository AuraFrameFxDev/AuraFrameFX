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
    /**
     * Returns a copy of this lock screen element with the specified visibility.
     *
     * @param visible Whether the element should be visible.
     * @return A new instance with updated visibility.
     */
    fun withVisibility(visible: Boolean): LockScreenElement {
        return copy(isVisible = visible)
    }
    
    /**
     * Returns a copy of this lock screen element with the specified custom text.
     *
     * @param text The new custom text, or null to remove it.
     * @return A new instance with the updated custom text.
     */
    fun withCustomText(text: String?): LockScreenElement {
        return copy(customText = text)
    }
}

/**
 * Converts this LockScreenElement to a LockScreenElementConfig instance.
 *
 * @return A LockScreenElementConfig with the same element ID, visibility, and custom text as this element.
 */
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

/**
 * Returns the background image source from the lock screen configuration, or null if not set.
 *
 * @return The background image source string, or null if unavailable.
 */
private fun LockScreenConfig?.safeBackground(): String? {
    return this?.backgroundConfig?.source
}

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Displays the main system customization screen for configuring Quick Settings and Lock Screen options.
 *
 * Collects configuration state from the provided ViewModel and presents UI sections for customizing quick settings tiles and lock screen elements, including shape, animation, visibility, custom text, and background images. Provides actions to reset settings to defaults and handles initial configuration loading.
 */
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

/**
 * Displays the customization UI for quick settings tiles and background.
 *
 * Allows users to modify the shape and animation of each quick settings tile and to change the background image if available.
 *
 * @param config The current quick settings configuration, or null if not loaded.
 * @param onTileShapeChange Callback invoked when a tile's shape is changed, receiving the tile ID and new shape.
 * @param onTileAnimationChange Callback invoked when a tile's animation is changed, receiving the tile ID and new animation.
 * @param onBackgroundChange Callback invoked when the background image is changed.
 */
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

/**
 * Displays the lock screen customization UI, allowing users to modify lock screen elements and background.
 *
 * Shows a list of lock screen elements for visibility toggling and custom text editing, and provides background image customization if available.
 *
 * @param config The current lock screen configuration, or null to display sample elements.
 * @param onElementChange Callback invoked when a lock screen element is changed.
 * @param onBackgroundChange Callback invoked when the background image is changed.
 */
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

/**
 * Displays customization options for a quick settings tile, allowing the user to select its shape and animation.
 *
 * @param tile The configuration for the quick settings tile to be customized.
 * @param onShapeChange Callback invoked when the tile shape is changed.
 * @param onAnimationChange Callback invoked when the tile animation is changed.
 */
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

/**
 * Displays customization options for a lock screen element, allowing visibility toggling and custom text editing.
 *
 * @param element The lock screen element to customize.
 * @param onElementChange Callback invoked when the element's visibility or custom text is updated.
 */
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

/**
 * Displays a UI card for customizing the background image, allowing the user to select or change the background.
 *
 * @param background The currently selected background image, or null if none is set.
 * @param onChange Callback invoked when the background image selection changes.
 */
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

/**
 * Displays a placeholder UI for selecting an overlay shape.
 *
 * Invokes the shape selection callback with the current shape when composed.
 *
 * @param currentShape The currently selected overlay shape.
 * @param onShapeSelected Callback invoked with the selected shape.
 */
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

/**
 * Displays a placeholder UI for selecting a quick settings animation.
 *
 * Invokes the provided callback with the current animation when composed.
 *
 * @param currentAnimation The currently selected animation.
 * @param onAnimationSelected Callback invoked with the selected animation.
 */
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

/**
 * Displays a placeholder UI for selecting a background image and notifies when an image is selected.
 *
 * @param currentImage The currently selected image, or null if none is selected.
 * @param onImageSelected Callback invoked with the current image when the composable is composed.
 */
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
