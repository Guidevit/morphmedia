package com.example.lhm3d.data.model

/**
 * Enum representing the quality level options for 3D model generation
 */
enum class ModelQuality {
    DRAFT,      // Lower quality, faster processing
    STANDARD,   // Medium quality, balanced processing time
    HIGH,       // High quality, longer processing time
    ULTRA;      // Ultra-high quality, longest processing time
    
    override fun toString(): String {
        return when (this) {
            DRAFT -> "DRAFT"
            STANDARD -> "STANDARD"
            HIGH -> "HIGH"
            ULTRA -> "ULTRA"
        }
    }
    
    companion object {
        fun fromString(value: String): ModelQuality {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                STANDARD
            }
        }
    }
}