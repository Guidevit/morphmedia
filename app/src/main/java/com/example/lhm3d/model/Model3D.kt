package com.example.lhm3d.model

import java.util.Date

/**
 * Represents a 3D human model created by the user.
 */
data class Model3D(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val sourceImageUrl: String = "",
    val thumbnailUrl: String = "",
    val modelStoragePath: String = "",
    val isPremium: Boolean = false,
    val processingStatus: ProcessingStatus = ProcessingStatus.PENDING,
    val errorMessage: String? = null,
    val lastAnimationId: String? = null,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Represents the processing status of a 3D model.
 */
enum class ProcessingStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}

/**
 * Represents the metadata associated with a 3D model.
 */
data class ModelMetadata(
    val width: Int = 0,
    val height: Int = 0,
    val depth: Int = 0,
    val vertexCount: Int = 0,
    val faceCount: Int = 0,
    val fileSize: Long = 0,
    val isAnimatable: Boolean = true
)

/**
 * Extension function to format the creation date of the model.
 */
fun Model3D.getFormattedDate(): String {
    val date = Date(createdAt)
    val day = date.date.toString().padStart(2, '0')
    val month = (date.month + 1).toString().padStart(2, '0')
    val year = date.year + 1900
    return "$day/$month/$year"
}
