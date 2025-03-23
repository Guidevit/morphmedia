package com.example.lhm3d.data.model

/**
 * Enum representing the processing status of a 3D model
 */
enum class ProcessingStatus {
    PENDING,       // Awaiting processing
    UPLOADING,     // Image is being uploaded
    QUEUED,        // Waiting in processing queue
    PROCESSING,    // Currently being processed
    COMPLETED,     // Successfully completed processing
    FAILED,        // Processing failed
    CANCELLED;     // Processing was cancelled
    
    override fun toString(): String {
        return when (this) {
            PENDING -> "PENDING"
            UPLOADING -> "UPLOADING"
            QUEUED -> "QUEUED"
            PROCESSING -> "PROCESSING"
            COMPLETED -> "COMPLETED"
            FAILED -> "FAILED"
            CANCELLED -> "CANCELLED"
        }
    }
}