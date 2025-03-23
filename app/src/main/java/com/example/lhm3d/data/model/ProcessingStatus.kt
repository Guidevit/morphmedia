package com.example.lhm3d.data.model

/**
 * Enum representing the processing status of a 3D model
 */
enum class ProcessingStatus {
    PENDING,       // Awaiting processing
    PROCESSING,    // Currently being processed
    COMPLETED,     // Successfully completed processing
    FAILED;        // Processing failed
    
    override fun toString(): String {
        return when (this) {
            PENDING -> "PENDING"
            PROCESSING -> "PROCESSING"
            COMPLETED -> "COMPLETED"
            FAILED -> "FAILED"
        }
    }
}