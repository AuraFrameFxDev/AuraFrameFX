package dev.aurakai.auraframefx.model.agent_states

// TODO: Define actual properties for these states/events.
// TODO: Classes reported as unused or need implementation. Ensure these are utilized by AuraShieldAgent.

public data class SecurityContextState(
    // Renamed to avoid clash with android.content.Context or other SecurityContext classes
    public val deviceRooted: Boolean? = null,
    public val selinuxMode: String? = null, // e.g., "Enforcing", "Permissive"
    public val harmfulAppScore: Float = 0.0f,
    // Add other relevant security context properties
)

public data class ActiveThreat(
    // Singular, as it will be in a list
    public val threatId: String,
    public val description: String,
    public val severity: Int, // e.g., 1-5
    public val recommendedAction: String? = null,
    // Add other relevant threat properties
)

public data class ScanEvent(
    public val eventId: String,
    public val timestamp: Long = System.currentTimeMillis(),
    public val scanType: String, // e.g., "AppScan", "FileSystemScan"
    public val findings: List<String> = emptyList(),
    // Add other relevant scan event properties
)
