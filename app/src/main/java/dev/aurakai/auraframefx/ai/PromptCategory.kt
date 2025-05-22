package dev.aurakai.auraframefx.ai

enum class PromptCategory {
    MOOD_WELLBEING,
    CREATIVITY,
    PRODUCTIVITY,
    LEARNING,
    GENERAL
}

fun getPrompt(category: PromptCategory): String {
    return when (category) {
        PromptCategory.MOOD_WELLBEING -> listOf(
            "How are you feeling today?",
            "What's on your mind?",
            "Describe your current mood."
        ).random()

        PromptCategory.CREATIVITY -> listOf(
            "Tell me a short story.",
            "What's a creative idea you have?",
            "Suggest a fun creative project."
        ).random()

        PromptCategory.PRODUCTIVITY -> listOf(
            "Help me plan my day.",
            "How can I be more productive?",
            "Suggest a good habit to start."
        ).random()

        PromptCategory.LEARNING -> listOf(
            "Teach me something new.",
            "What's an interesting fact?",
            "Explain a concept to me."
        ).random()

        PromptCategory.GENERAL -> listOf(
            "What's the news?",
            "Tell me a joke.",
            "What's trending today?"
        ).random()
    }
}