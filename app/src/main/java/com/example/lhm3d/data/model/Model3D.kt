package com.example.lhm3d.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Model quality tier based on subscription level
 */
enum class ModelTier {
    FREE,           // Free tier with limited features
    PREMIUM         // Premium tier with all features
}

/**
 * Data class representing a 3D human model created by LHM algorithm
 */
@Parcelize
data class Model3D(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val creationDate: Date = Date(),
    val lastModified: Date = Date(),
    val status: ProcessingStatus = ProcessingStatus.UPLOADING,
    val sourceImageUrl: String = "",
    val modelUrl: String = "",
    val thumbnailUrl: String = "",
    val modelTier: ModelTier = ModelTier.FREE,
    val isPublic: Boolean = false,
    val isRigged: Boolean = false,
    val hasAnimation: Boolean = false,
    val animationCount: Int = 0
) : Parcelable