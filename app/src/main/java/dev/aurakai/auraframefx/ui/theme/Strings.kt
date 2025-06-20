package dev.aurakai.auraframefx.ui.theme

/**
 * String resources for the AuraFrameFX app
 * Using Kotlin object instead of strings.xml for better integration with Compose
 */
public object AppStrings {
    // App Information
    public const val APP_NAME = "AuraFrameFX"
    public const val APP_DESCRIPTION = "AuraFrameFX hooks and enhancements"

    // Common UI Elements
    public const val BUTTON_CONTINUE = "Continue"
    public const val BUTTON_CANCEL = "Cancel"
    public const val BUTTON_OK = "OK"
    public const val BUTTON_SUBMIT = "Submit"
    public const val BUTTON_NEXT = "Next"
    public const val BUTTON_PREVIOUS = "Previous"

    // Navigation
    public const val NAV_HOME = "Home"
    public const val NAV_PROFILE = "Profile"
    public const val NAV_SETTINGS = "Settings"
    public const val NAV_AI_CHAT = "AI Chat"

    // Feature Specific Text
    public const val AI_CHAT_PLACEHOLDER = "Type your message here..."
    public const val AI_CHAT_SEND = "Send"
    public const val AI_CHAT_CLEAR = "Clear Chat"

    // Settings
    public const val SETTINGS_THEME = "App Theme"
    public const val SETTINGS_NOTIFICATIONS = "Notifications"
    public const val SETTINGS_PRIVACY = "Privacy"

    // Error Messages
    public const val ERROR_NETWORK = "Network error. Please check your connection."
    public const val ERROR_GENERAL = "Something went wrong. Please try again."
}
