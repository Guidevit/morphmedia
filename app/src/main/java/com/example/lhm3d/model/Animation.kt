package com.example.lhm3d.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing an animation that can be applied to 3D models
 */
@Parcelize
data class Animation(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val previewUrl: String = "",
    val thumbnailUrl: String = "",
    val duration: Float = 0f,
    val category: String = "",
    val isPublic: Boolean = true,
    val isFreemium: Boolean = false, // Free animations available to all users
    val tags: List<String> = emptyList()
) : Parcelable

/**
 * Predefined basic animations available in the app
 */
val BASIC_ANIMATIONS = listOf(
    Animation(
        id = "walking",
        name = "Walking",
        description = "Basic walking animation",
        duration = 3.0f,
        category = "Locomotion",
        isFreemium = true,
        tags = listOf("walk", "movement", "basic")
    ),
    Animation(
        id = "running",
        name = "Running",
        description = "Fast running animation",
        duration = 2.0f,
        category = "Locomotion",
        isFreemium = true,
        tags = listOf("run", "movement", "basic")
    ),
    Animation(
        id = "jumping",
        name = "Jumping",
        description = "Simple jump animation",
        duration = 1.5f,
        category = "Action",
        isFreemium = true,
        tags = listOf("jump", "action", "basic")
    ),
    Animation(
        id = "waving",
        name = "Waving",
        description = "Friendly waving gesture",
        duration = 2.0f,
        category = "Gesture",
        isFreemium = true,
        tags = listOf("wave", "greeting", "basic")
    ),
    Animation(
        id = "dance_simple",
        name = "Simple Dance",
        description = "Basic dance moves",
        duration = 4.0f,
        category = "Dance",
        isFreemium = true,
        tags = listOf("dance", "movement", "basic")
    )
)

/**
 * Data class representing animation settings for a saved animation
 */
@Parcelize
data class AnimationSettings(
    val loop: Boolean = false,
    val speed: Float = 1.0f,
    val startFrame: Int = 0,
    val endFrame: Int = -1 // -1 means play to the end
) : Parcelable

/**
 * Data class representing a saved animation with reference to a model and animation
 */
@Parcelize
data class SavedAnimation(
    val id: String = "",
    val userId: String = "",
    val modelId: String = "",
    val animationId: String = "",
    val name: String = "",
    val settings: AnimationSettings = AnimationSettings()
) : Parcelable