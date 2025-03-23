package com.example.lhm3d.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Processing status of a 3D model
 */
enum class ProcessingStatus {
    UPLOADING,      // Image is being uploaded
    QUEUED,         // Waiting in processing queue
    PROCESSING,     // Being processed by LHM algorithm
    COMPLETED,      // Processing finished successfully
    FAILED,         // Processing failed
    CANCELLED       // Processing was cancelled
}

/**
 * Subscription type for model creation
 */
enum class SubscriptionType {
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
    val subscriptionType: SubscriptionType = SubscriptionType.FREE,
    val isPublic: Boolean = false,
    val isRigged: Boolean = false,
    val hasAnimation: Boolean = false,
    val animationCount: Int = 0
) : Parcelable