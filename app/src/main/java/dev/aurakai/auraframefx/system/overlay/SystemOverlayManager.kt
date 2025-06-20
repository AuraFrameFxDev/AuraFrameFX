package dev.aurakai.auraframefx.system.overlay

// These will be actual imports once model files are created in dev.aurakai.auraframefx.system.overlay.model
import dev.aurakai.auraframefx.system.overlay.model.OverlayTheme
import dev.aurakai.auraframefx.system.overlay.model.OverlayElement
import dev.aurakai.auraframefx.system.overlay.model.OverlayAnimation
import dev.aurakai.auraframefx.system.overlay.model.OverlayTransition
import dev.aurakai.auraframefx.system.overlay.model.OverlayShape // Assuming this is the type for applyShape
import dev.aurakai.auraframefx.system.overlay.model.SystemOverlayConfig


public interface SystemOverlayManager {
    public fun applyTheme(theme: OverlayTheme)
    public fun applyElement(element: OverlayElement)
    public fun applyAnimation(animation: OverlayAnimation)
    public fun applyTransition(transition: OverlayTransition)
    public fun applyShape(shape: OverlayShape) // Changed from OverlayShapeConfig to OverlayShape based on Impl
    public fun applyConfig(config: SystemOverlayConfig)
    public fun removeElement(elementId: String)
    public fun clearAll()
    // fun generateOverlayFromDescription(description: String): SystemOverlayConfig // Still commented out
}
