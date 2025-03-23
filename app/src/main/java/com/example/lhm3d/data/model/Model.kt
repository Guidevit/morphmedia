package com.example.lhm3d.data.model

import java.util.Date

/**
 * Data class representing a 3D model created using the LHM model.
 */
data class Model(
    val id: String,
    val name: String,
    val imageUrl: String,
    val modelUrl: String,
    val creatorId: String,
    val createdAt: Date,
    val isPublic: Boolean = false,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val animationUrls: List<String> = emptyList()
)