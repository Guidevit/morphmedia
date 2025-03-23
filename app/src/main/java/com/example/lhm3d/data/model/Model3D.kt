package com.example.lhm3d.data.model

import java.util.Date

enum class ProcessingStatus {
    QUEUED,      // Initial state, waiting for processing
    PROCESSING,  // Currently being processed by the LHM model
    COMPLETED,   // Successfully processed
    FAILED,      // Processing failed
    CANCELLED    // User cancelled the processing
}

data class Model3D(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val creatorId: String = "",
    val creatorName: String = "",
    val thumbnailUrl: String = "",
    val modelUrl: String = "",
    val textureUrl: String = "",
    val originalImageUrl: String = "",
    val status: ProcessingStatus = ProcessingStatus.QUEUED,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isPublic: Boolean = false,
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    val tags: List<String> = listOf()
)