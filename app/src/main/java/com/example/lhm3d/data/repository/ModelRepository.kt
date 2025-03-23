package com.example.lhm3d.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lhm3d.data.model.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

/**
 * Repository for handling Model data operations with Firebase.
 */
class ModelRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val modelsCollection = firestore.collection("models")
    
    // Cache of user models
    private val _userModels = MutableLiveData<List<Model>>()
    val userModels: LiveData<List<Model>> = _userModels
    
    // Cache of community models
    private val _communityModels = MutableLiveData<List<Model>>()
    val communityModels: LiveData<List<Model>> = _communityModels
    
    /**
     * Fetch models created by the current user.
     */
    suspend fun fetchUserModels() {
        try {
            val currentUserId = auth.currentUser?.uid ?: return
            
            val snapshots = modelsCollection
                .whereEqualTo("creatorId", currentUserId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                
            val models = snapshots.documents.mapNotNull { doc ->
                doc.toObject(Model::class.java)
            }
            
            _userModels.postValue(models)
        } catch (e: Exception) {
            // Handle error
            _userModels.postValue(emptyList())
        }
    }
    
    /**
     * Fetch public models shared by the community.
     */
    suspend fun fetchCommunityModels() {
        try {
            val snapshots = modelsCollection
                .whereEqualTo("isPublic", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50) // Limit to recent 50 models
                .get()
                .await()
                
            val models = snapshots.documents.mapNotNull { doc ->
                doc.toObject(Model::class.java)
            }
            
            _communityModels.postValue(models)
        } catch (e: Exception) {
            // Handle error
            _communityModels.postValue(emptyList())
        }
    }
    
    /**
     * Create a new 3D model from an image.
     * @param name The name of the model
     * @param imageUri The URI of the image
     * @param description Optional description of the model
     * @return The ID of the created model
     */
    suspend fun createModel(name: String, imageUri: Uri, description: String? = null): String {
        val currentUserId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        
        // Upload image to Firebase Storage
        val imagePath = "images/${UUID.randomUUID()}.jpg"
        val imageRef = storage.reference.child(imagePath)
        imageRef.putFile(imageUri).await()
        val imageUrl = imageRef.downloadUrl.await().toString()
        
        // Create model document
        val modelId = UUID.randomUUID().toString()
        val model = Model(
            id = modelId,
            name = name,
            imageUrl = imageUrl,
            modelUrl = "", // Will be updated after model processing
            creatorId = currentUserId,
            createdAt = Date(),
            description = description
        )
        
        // Save to Firestore
        modelsCollection.document(modelId).set(model).await()
        
        // Queue model for processing (in a real app, this would use Cloud Functions)
        // For now, we'll simulate this with a delay
        
        return modelId
    }
    
    /**
     * Get a model by ID.
     * @param modelId The ID of the model to retrieve
     * @return The model if found, or null
     */
    suspend fun getModelById(modelId: String): Model? {
        return try {
            val doc = modelsCollection.document(modelId).get().await()
            doc.toObject(Model::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Update a model's details.
     * @param model The updated model
     */
    suspend fun updateModel(model: Model) {
        modelsCollection.document(model.id).set(model).await()
    }
    
    /**
     * Delete a model.
     * @param modelId The ID of the model to delete
     */
    suspend fun deleteModel(modelId: String) {
        modelsCollection.document(modelId).delete().await()
    }
}