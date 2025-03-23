package com.example.lhm3d.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

/**
 * Singleton class to handle Firebase services
 */
object FirebaseManager {
    // Firebase Auth instance
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    
    // Firestore instance
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    
    // Storage instance
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    
    // Collection references
    private val usersCollection by lazy { firestore.collection("users") }
    private val modelsCollection by lazy { firestore.collection("models") }
    
    // Storage references
    private val modelsStorageRef by lazy { storage.reference.child("models") }
    private val thumbnailsStorageRef by lazy { storage.reference.child("thumbnails") }
    private val texturesStorageRef by lazy { storage.reference.child("textures") }
    private val imagesStorageRef by lazy { storage.reference.child("images") }
    
    // Auth functions
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    fun isUserSignedIn(): Boolean = auth.currentUser != null
    
    fun getUserId(): String = auth.currentUser?.uid ?: ""
    
    // Firestore references
    fun getModelsCollection() = modelsCollection
    
    fun getUserModelsQuery() = modelsCollection.whereEqualTo("creatorId", getUserId())
    
    fun getRecentModelsQuery() = modelsCollection
        .whereEqualTo("isPublic", true)
        .orderBy("createdAt", Query.Direction.DESCENDING)
    
    fun getPopularModelsQuery() = modelsCollection
        .whereEqualTo("isPublic", true)
        .orderBy("viewCount", Query.Direction.DESCENDING)
    
    // Storage references
    fun getModelStorageRef(modelId: String): StorageReference = modelsStorageRef.child(modelId)
    
    fun getThumbnailStorageRef(modelId: String): StorageReference = thumbnailsStorageRef.child(modelId)
    
    fun getTextureStorageRef(modelId: String): StorageReference = texturesStorageRef.child(modelId)
    
    fun getImageStorageRef(modelId: String): StorageReference = imagesStorageRef.child(modelId)
}