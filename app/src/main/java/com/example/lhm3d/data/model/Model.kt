package com.example.lhm3d.data.model

import java.util.Date

/**
 * Data class representing a 3D model.
 */
data class Model(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val imageUrl: String = "", // URL to the source image
    val modelUrl: String = "", // URL to the 3D model file
    val thumbnailUrl: String = "", // URL to the model thumbnail
    val creatorId: String = "",
    val creatorName: String = "",
    val createdAt: Date = Date(),
    val isPublic: Boolean = false,
    val likes: Int = 0,
    val isProcessing: Boolean = false,
    val animationUrl: String? = null, // URL to the animation file
    val tags: List<String> = emptyList(),
    val viewCount: Int = 0
)