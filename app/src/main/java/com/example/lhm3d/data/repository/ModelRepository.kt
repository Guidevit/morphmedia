package com.example.lhm3d.data.repository

import com.example.lhm3d.data.model.Model3D
import com.example.lhm3d.data.model.ProcessingStatus
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.io.File

class ModelRepository {
    
    /**
     * Get a list of recent models
     */
    suspend fun getRecentModels(limit: Int = 10): List<Model3D> {
        return try {
            val querySnapshot = FirebaseManager.getRecentModelsQuery()
                .limit(limit.toLong())
                .get()
                .await()
                
            querySnapshot.documents.mapNotNull { it.toModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get a list of popular models
     */
    suspend fun getPopularModels(limit: Int = 10): List<Model3D> {
        return try {
            val querySnapshot = FirebaseManager.getPopularModelsQuery()
                .limit(limit.toLong())
                .get()
                .await()
                
            querySnapshot.documents.mapNotNull { it.toModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get a list of user's models
     */
    suspend fun getUserModels(): List<Model3D> {
        return try {
            val querySnapshot = FirebaseManager.getUserModelsQuery()
                .get()
                .await()
                
            querySnapshot.documents.mapNotNull { it.toModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get a model by ID
     */
    suspend fun getModelById(modelId: String): Model3D? {
        return try {
            val document = FirebaseManager.getModelsCollection()
                .document(modelId)
                .get()
                .await()
                
            document.toModel()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Create a new model from an image file
     */
    suspend fun createModelFromImage(name: String, description: String, imageFile: File, isPublic: Boolean): Model3D? {
        try {
            // 1. Create a new model document in Firestore
            val modelData = hashMapOf(
                "name" to name,
                "description" to description,
                "creatorId" to FirebaseManager.getUserId(),
                "creatorName" to (FirebaseManager.getCurrentUser()?.displayName ?: "Anonymous"),
                "status" to ProcessingStatus.QUEUED.name,
                "isPublic" to isPublic,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "updatedAt" to com.google.firebase.Timestamp.now(),
                "viewCount" to 0,
                "likeCount" to 0,
                "tags" to listOf<String>()
            )
            
            val modelRef = FirebaseManager.getModelsCollection().document()
            val modelId = modelRef.id
            modelRef.set(modelData).await()
            
            // 2. Upload the image file to Storage
            val imageRef = FirebaseManager.getImageStorageRef(modelId)
            val uploadTask = imageRef.putFile(android.net.Uri.fromFile(imageFile)).await()
            val imageUrl = uploadTask.storage.downloadUrl.await().toString()
            
            // 3. Update the model document with the image URL
            val updates = hashMapOf<String, Any>(
                "originalImageUrl" to imageUrl,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            modelRef.update(updates).await()
            
            // 4. Return the created model
            return getModelById(modelId)
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Convert a Firestore document to a Model3D object
     */
    private fun DocumentSnapshot.toModel(): Model3D? {
        val model = toObject<Model3D>()
        model?.let {
            // Add document ID as model ID if needed
            return it.copy(id = id)
        }
        return model
    }
}