package com.example.lhm3d.model

/**
 * Represents an animation that can be applied to a 3D model.
 */
data class Animation(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: AnimationCategory = AnimationCategory.BASIC,
    val duration: Float = 0f,
    val isPremium: Boolean = false,
    val previewUrl: String? = null,
    val motionDataPath: String = ""
)

/**
 * Represents the category of an animation.
 */
enum class AnimationCategory {
    BASIC,
    WALKING,
    DANCING,
    SPORTS,
    POSES,
    CUSTOM
}

/**
 * Represents a saved animation for a specific model.
 */
data class SavedAnimation(
    val id: String = "",
    val userId: String = "",
    val modelId: String = "",
    val animationId: String = "",
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val settings: AnimationSettings = AnimationSettings()
)

/**
 * Represents settings for an animation.
 */
data class AnimationSettings(
    val speed: Float = 1.0f,
    val loop: Boolean = true,
    val startFrame: Int = 0,
    val endFrame: Int = -1 // -1 means the entire animation
)

/**
 * List of basic animations available in the app.
 */
val BASIC_ANIMATIONS = listOf(
    Animation(
        id = "walk",
        name = "Walk",
        description = "Basic walking animation",
        category = AnimationCategory.WALKING,
        duration = 2.0f,
        isPremium = false
    ),
    Animation(
        id = "run",
        name = "Run",
        description = "Fast running animation",
        category = AnimationCategory.WALKING,
        duration = 1.5f,
        isPremium = false
    ),
    Animation(
        id = "jump",
        name = "Jump",
        description = "Simple jumping animation",
        category = AnimationCategory.BASIC,
        duration = 1.0f,
        isPremium = false
    ),
    Animation(
        id = "dance",
        name = "Dance",
        description = "Fun dancing animation",
        category = AnimationCategory.DANCING,
        duration = 3.0f,
        isPremium = false
    ),
    Animation(
        id = "wave",
        name = "Wave",
        description = "Hand waving animation",
        category = AnimationCategory.BASIC,
        duration = 1.2f,
        isPremium = false
    ),
    Animation(
        id = "idle",
        name = "Idle",
        description = "Idle standing animation with subtle movements",
        category = AnimationCategory.BASIC,
        duration = 4.0f,
        isPremium = false
    ),
    Animation(
        id = "backflip",
        name = "Backflip",
        description = "Advanced backflip animation",
        category = AnimationCategory.SPORTS,
        duration = 2.0f,
        isPremium = true
    ),
    Animation(
        id = "salsa",
        name = "Salsa Dance",
        description = "Salsa dancing animation",
        category = AnimationCategory.DANCING,
        duration = 5.0f,
        isPremium = true
    )
)
