package com.example.lhm3d.model

/**
 * Represents a user in the LHM 3D Creator app.
 */
data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val subscription: SubscriptionType = SubscriptionType.FREE_TRIAL,
    val createdAt: Long = System.currentTimeMillis(),
    val remainingCredits: Int = 5, // For free trial
    val settings: UserSettings = UserSettings()
)

/**
 * Represents the subscription type for a user.
 */
enum class SubscriptionType {
    FREE_TRIAL,
    PREMIUM_MONTHLY,
    PREMIUM_YEARLY;
    
    /**
     * Check if the subscription is a premium type
     */
    fun isPremium(): Boolean {
        return this == PREMIUM_MONTHLY || this == PREMIUM_YEARLY
    }
    
    /**
     * Convert to ModelTier for rendering quality settings
     */
    fun toModelTier(): com.example.lhm3d.data.model.ModelTier {
        return if (isPremium()) {
            com.example.lhm3d.data.model.ModelTier.PREMIUM
        } else {
            com.example.lhm3d.data.model.ModelTier.FREE
        }
    }
}

/**
 * Represents user settings for the app.
 */
data class UserSettings(
    val notifications: Boolean = true,
    val autoSave: Boolean = true,
    val highQualityRendering: Boolean = false
)
