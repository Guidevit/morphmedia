package com.example.lhm3d.data.model

/**
 * Enum representing the different processing states of a 3D model
 */
enum class ProcessingStatus {
    PENDING,       // Initial state, waiting to be processed
    PROCESSING,    // Currently being processed
    COMPLETED,     // Processing completed successfully
    FAILED         // Processing failed with error
}