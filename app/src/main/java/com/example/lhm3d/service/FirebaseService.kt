package com.example.lhm3d.service

import android.net.Uri
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.SubscriptionType
import com.example.lhm3d.model.User
import com.example.lhm3d.model.UserSettings
import com.example.lhm3d.repository.ModelsRepository
import com.example.lhm3d.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Service layer for Firebase operations.
 * This service centralizes all Firebase-related operations.
 */
class FirebaseService {
    private val userRepository = UserRepository()
    private val modelsRepository = ModelsRepository()

    // User authentication methods
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return userRepository.signIn(email, password).map { }
    }

    suspend fun signUp(email: String, password: String, displayName: String): Result<Unit> {
        return userRepository.signUp(email, password, displayName).map { }
    }

    fun signOut() {
        userRepository.signOut()
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return userRepository.resetPassword(email)
    }

    fun isUserLoggedIn(): Boolean {
        return userRepository.getCurrentUser() != null
    }

    fun getCurrentUserId(): String? {
        return userRepository.getCurrentUser()?.uid
    }

    // User data methods
    fun getUserData(): Flow<Result<User>> {
        val userId = getCurrentUserId() ?: return Flow { emit(Result.failure(Exception("User not logged in"))) }
        return userRepository.getUserData(userId)
    }

    suspend fun updateUserSettings(settings: UserSettings): Result<Unit> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return userRepository.updateUserSettings(userId, settings)
    }

    suspend fun updateSubscription(subscriptionType: SubscriptionType): Result<Unit> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return userRepository.updateSubscription(userId, subscriptionType)
    }

    suspend fun decreaseCredits(): Result<Int> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return userRepository.decreaseCredits(userId)
    }

    // Model management methods
    suspend fun uploadSourceImage(imageUri: Uri, imageName: String): Result<Model3D> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return modelsRepository.uploadSourceImage(userId, imageUri, imageName)
    }

    fun getUserModels(): Flow<Result<List<Model3D>>> {
        val userId = getCurrentUserId() ?: return Flow { emit(Result.failure(Exception("User not logged in"))) }
        return modelsRepository.getUserModels(userId)
    }

    fun getRecentModels(limit: Int = 5): Flow<Result<List<Model3D>>> {
        val userId = getCurrentUserId() ?: return Flow { emit(Result.failure(Exception("User not logged in"))) }
        return modelsRepository.getRecentModels(userId, limit)
    }

    suspend fun getModel(modelId: String): Result<Model3D> {
        return modelsRepository.getModel(modelId)
    }

    suspend fun saveModelData(modelId: String, modelFile: File, thumbnailUri: Uri): Result<Model3D> {
        return modelsRepository.saveModelData(modelId, modelFile, thumbnailUri)
    }

    suspend fun deleteModel(modelId: String): Result<Unit> {
        return modelsRepository.deleteModel(modelId)
    }

    // Animation methods
    fun getAvailableAnimations() = modelsRepository.getAvailableAnimations()
    
    suspend fun saveAnimation(modelId: String, animationId: String, name: String) {
        val userId = getCurrentUserId() ?: throw Exception("User not logged in")
        val savedAnimation = com.example.lhm3d.model.SavedAnimation(
            userId = userId,
            modelId = modelId,
            animationId = animationId,
            name = name
        )
        modelsRepository.saveAnimation(savedAnimation)
    }
    
    fun getModelAnimations(modelId: String) = modelsRepository.getModelAnimations(modelId)
}
