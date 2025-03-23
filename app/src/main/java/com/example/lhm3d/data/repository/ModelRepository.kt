package com.example.lhm3d.data.repository

import android.content.Context
import android.net.Uri
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.ProcessingStatus
import com.example.lhm3d.model.ModelTier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date

/**
 * Repository for Model3D data operations
 */
class ModelRepository(private val context: Context) {
    
    private val firebaseManager = FirebaseManager.getInstance(context)
    
    /**
     * Fetch all models belonging to the current user
     */
    fun getUserModels(): Flow<List<Model3D>> = flow {
        val models = firebaseManager.getUserModels()
        emit(models)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Fetch public models that can be viewed by anyone
     */
    fun getPublicModels(): Flow<List<Model3D>> = flow {
        val models = firebaseManager.getPublicModels()
        emit(models)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get a specific model by its ID
     */
    fun getModelById(modelId: String): Flow<Model3D?> = flow {
        val model = firebaseManager.getModelById(modelId)
        emit(model)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Create a new model with an image source
     */
    fun createModelFromImage(
        name: String,
        description: String,
        imageUri: Uri,
        isPublic: Boolean = false,
        modelTier: ModelTier = ModelTier.FREE
    ): Flow<String> = flow {
        // Create initial model object
        val currentUser = firebaseManager.getCurrentUser()
        val model = Model3D(
            userId = currentUser?.uid ?: "",
            name = name,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            processingStatus = ProcessingStatus.PENDING,
            isPremium = modelTier == ModelTier.PREMIUM,
            metadata = mapOf("description" to description)
        )
        
        // Save model to get ID
        val modelId = firebaseManager.saveModel(model)
        
        // Upload image
        val imageUrl = firebaseManager.uploadSourceImage(modelId, imageUri)
        
        // Update model with image URL and status
        val updatedModel = model.copy(
            id = modelId,
            sourceImageUrl = imageUrl,
            processingStatus = ProcessingStatus.PROCESSING
        )
        firebaseManager.saveModel(updatedModel)
        
        // Return the model ID
        emit(modelId)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Delete a model by its ID
     */
    fun deleteModel(modelId: String): Flow<Boolean> = flow {
        try {
            firebaseManager.deleteModel(modelId)
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Update model status
     */
    fun updateModelStatus(modelId: String, status: ProcessingStatus): Flow<Boolean> = flow {
        try {
            val model = firebaseManager.getModelById(modelId)
            if (model != null) {
                val updatedModel = model.copy(
                    processingStatus = status,
                    updatedAt = System.currentTimeMillis()
                )
                firebaseManager.saveModel(updatedModel)
                emit(true)
            } else {
                emit(false)
            }
        } catch (e: Exception) {
            emit(false)
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Update model properties
     */
    fun updateModel(model: Model3D): Flow<Boolean> = flow {
        try {
            val updatedModel = model.copy(updatedAt = System.currentTimeMillis())
            firebaseManager.saveModel(updatedModel)
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get most recent models, limited by count
     */
    suspend fun getRecentModels(limit: Int): List<Model3D> {
        return firebaseManager.getUserModels().sortedByDescending { it.createdAt }.take(limit)
    }
}