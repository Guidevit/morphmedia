package com.example.lhm3d.repository

import android.net.Uri
import com.example.lhm3d.model.Animation
import com.example.lhm3d.model.BASIC_ANIMATIONS
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.ProcessingStatus
import com.example.lhm3d.model.SavedAnimation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Repository for managing 3D models and animations.
 */
class ModelsRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val modelsCollection = firestore.collection("models")
    private val savedAnimationsCollection = firestore.collection("saved_animations")
    
    /**
     * Upload a source image to create a 3D model.
     */
    suspend fun uploadSourceImage(userId: String, imageUri: Uri, imageName: String): Result<Model3D> = withContext(Dispatchers.IO) {
        try {
            // Create a storage reference
            val storageRef = storage.reference.child("users/$userId/source_images/$imageName")
            
            // Upload the file
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            // Create a new model entry in Firestore
            val modelId = UUID.randomUUID().toString()
            val model = Model3D(
                id = modelId,
                userId = userId,
                name = "Model $imageName",
                sourceImageUrl = downloadUrl,
                processingStatus = ProcessingStatus.PENDING
            )
            
            modelsCollection.document(modelId).set(model).await()
            return@withContext Result.success(model)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Get all models for a user.
     */
    fun getUserModels(userId: String): Flow<Result<List<Model3D>>> = flow {
        try {
            val querySnapshot = modelsCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val models = querySnapshot.documents.mapNotNull { 
                it.toObject(Model3D::class.java) 
            }
            emit(Result.success(models))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get recent models for a user.
     */
    fun getRecentModels(userId: String, limit: Int = 5): Flow<Result<List<Model3D>>> = flow {
        try {
            val querySnapshot = modelsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("processingStatus", ProcessingStatus.COMPLETED)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val models = querySnapshot.documents.mapNotNull { 
                it.toObject(Model3D::class.java) 
            }
            emit(Result.success(models))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get a specific model by ID.
     */
    suspend fun getModel(modelId: String): Result<Model3D> = withContext(Dispatchers.IO) {
        try {
            val documentSnapshot = modelsCollection.document(modelId).get().await()
            val model = documentSnapshot.toObject(Model3D::class.java)
            if (model != null) {
                return@withContext Result.success(model)
            } else {
                return@withContext Result.failure(Exception("Model not found"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Update the processing status of a model.
     */
    suspend fun updateModelStatus(modelId: String, status: ProcessingStatus, errorMessage: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updates = mutableMapOf<String, Any>(
                "processingStatus" to status,
                "updatedAt" to System.currentTimeMillis()
            )
            
            errorMessage?.let {
                updates["errorMessage"] = it
            }
            
            modelsCollection.document(modelId).update(updates).await()
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Save the model data after processing.
     */
    suspend fun saveModelData(modelId: String, modelFile: File, thumbnailUri: Uri): Result<Model3D> = withContext(Dispatchers.IO) {
        try {
            // Upload model file
            val modelStorageRef = storage.reference.child("models/$modelId/model.glb")
            val modelUploadTask = modelStorageRef.putFile(Uri.fromFile(modelFile)).await()
            val modelDownloadUrl = modelStorageRef.downloadUrl.await().toString()
            
            // Upload thumbnail
            val thumbnailStorageRef = storage.reference.child("models/$modelId/thumbnail.jpg")
            val thumbnailUploadTask = thumbnailStorageRef.putFile(thumbnailUri).await()
            val thumbnailDownloadUrl = thumbnailStorageRef.downloadUrl.await().toString()
            
            // Update model in Firestore
            val updates = mapOf(
                "modelStoragePath" to modelDownloadUrl,
                "thumbnailUrl" to thumbnailDownloadUrl,
                "processingStatus" to ProcessingStatus.COMPLETED,
                "updatedAt" to System.currentTimeMillis()
            )
            
            modelsCollection.document(modelId).update(updates).await()
            
            // Get the updated model
            return@withContext getModel(modelId)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Delete a model.
     */
    suspend fun deleteModel(modelId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Get the model to delete its storage files
            val model = getModel(modelId).getOrThrow()
            
            // Delete storage files
            if (model.sourceImageUrl.isNotEmpty()) {
                storage.getReferenceFromUrl(model.sourceImageUrl).delete().await()
            }
            
            if (model.modelStoragePath.isNotEmpty()) {
                storage.getReferenceFromUrl(model.modelStoragePath).delete().await()
            }
            
            if (model.thumbnailUrl.isNotEmpty()) {
                storage.getReferenceFromUrl(model.thumbnailUrl).delete().await()
            }
            
            // Delete document
            modelsCollection.document(modelId).delete().await()
            
            // Delete associated saved animations
            val animationsQuery = savedAnimationsCollection.whereEqualTo("modelId", modelId).get().await()
            for (document in animationsQuery.documents) {
                savedAnimationsCollection.document(document.id).delete().await()
            }
            
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Get available animations.
     */
    fun getAvailableAnimations(): Flow<Result<List<Animation>>> = flow {
        // In a real app, this would fetch from Firestore
        // For now, we'll use the predefined list
        emit(Result.success(BASIC_ANIMATIONS))
    }.flowOn(Dispatchers.IO)
    
    /**
     * Save a user's animation preference.
     */
    suspend fun saveAnimation(savedAnimation: SavedAnimation): Result<SavedAnimation> = withContext(Dispatchers.IO) {
        try {
            // Check if there's already a saved animation with this name for the model
            val existingQuery = savedAnimationsCollection
                .whereEqualTo("userId", savedAnimation.userId)
                .whereEqualTo("modelId", savedAnimation.modelId)
                .whereEqualTo("name", savedAnimation.name)
                .get()
                .await()
            
            val id = if (existingQuery.documents.isEmpty()) {
                UUID.randomUUID().toString()
            } else {
                existingQuery.documents[0].id
            }
            
            val animation = savedAnimation.copy(id = id)
            savedAnimationsCollection.document(id).set(animation).await()
            
            // Update the model's last animation
            modelsCollection.document(savedAnimation.modelId)
                .update("lastAnimationId", id)
                .await()
            
            return@withContext Result.success(animation)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Get saved animations for a model.
     */
    fun getModelAnimations(modelId: String): Flow<Result<List<SavedAnimation>>> = flow {
        try {
            val querySnapshot = savedAnimationsCollection
                .whereEqualTo("modelId", modelId)
                .get()
                .await()
            
            val animations = querySnapshot.documents.mapNotNull {
                it.toObject(SavedAnimation::class.java)
            }
            emit(Result.success(animations))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
