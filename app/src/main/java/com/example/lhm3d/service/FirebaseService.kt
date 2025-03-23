package com.example.lhm3d.service

import android.net.Uri
import android.util.Log
import com.example.lhm3d.model.Animation
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.SubscriptionType
import com.example.lhm3d.model.User
import com.example.lhm3d.model.UserSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Service for interacting with Firebase services including Authentication and Firestore
 */
class FirebaseService {
    
    private val TAG = "FirebaseService"
    
    // Firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    // Collection references
    private val usersCollection = firestore.collection("users")
    private val modelsCollection = firestore.collection("models")
    private val animationsCollection = firestore.collection("animations")
    
    /**
     * Get the current user ID or null if not logged in
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Update the user's subscription in Firestore
     * @param subscriptionType The new subscription type
     */
    suspend fun updateSubscription(subscriptionType: SubscriptionType) {
        try {
            val userId = getCurrentUserId() ?: return
            
            val subscriptionData = hashMapOf(
                "subscriptionType" to subscriptionType.toString(),
                "isPremium" to subscriptionType.isPremium(),
                "updatedAt" to System.currentTimeMillis()
            )
            
            usersCollection.document(userId)
                .collection("subscription")
                .document("current")
                .set(subscriptionData)
                .await()
                
            // Also update the main user document
            usersCollection.document(userId)
                .update("subscriptionType", subscriptionType.toString())
                .await()
                
            Log.d(TAG, "Subscription updated to $subscriptionType")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating subscription", e)
        }
    }
    
    /**
     * Get the current user's subscription type
     * @return The subscription type, defaults to FREE_TRIAL if not found
     */
    suspend fun getSubscriptionType(): SubscriptionType {
        try {
            val userId = getCurrentUserId() ?: return SubscriptionType.FREE_TRIAL
            
            val snapshot = usersCollection.document(userId)
                .collection("subscription")
                .document("current")
                .get()
                .await()
                
            val subscriptionString = snapshot.getString("subscriptionType")
            
            return when (subscriptionString) {
                "Premium Monthly" -> SubscriptionType.PREMIUM_MONTHLY
                "Premium Yearly" -> SubscriptionType.PREMIUM_YEARLY
                else -> SubscriptionType.FREE_TRIAL
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting subscription type", e)
            return SubscriptionType.FREE_TRIAL
        }
    }
    
    /**
     * Check if the user has a premium subscription
     * @return True if premium, false otherwise
     */
    suspend fun isPremiumUser(): Boolean {
        return getSubscriptionType().isPremium()
    }
    
    /**
     * Get a model by ID
     * @param modelId The ID of the model to retrieve
     * @return Result with Model3D if successful, or an exception if failed
     */
    suspend fun getModel(modelId: String): Result<Model3D> {
        return try {
            val document = modelsCollection.document(modelId).get().await()
            
            if (document.exists()) {
                val model = document.toObject(Model3D::class.java)
                if (model != null) {
                    Result.success(model)
                } else {
                    Result.failure(Exception("Failed to convert document to Model3D"))
                }
            } else {
                Result.failure(Exception("Model not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting model $modelId", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get a flow of available animations
     * @return Flow emitting Result with animations list
     */
    fun getAvailableAnimations(): Flow<Result<List<Animation>>> = flow {
        try {
            val userId = getCurrentUserId()
            
            // Query animations - either public ones or owned by the user
            val query = if (userId != null) {
                animationsCollection
                    .whereEqualTo("isPublic", true)
                    .orderBy("name", Query.Direction.ASCENDING)
            } else {
                animationsCollection
                    .whereEqualTo("isPublic", true)
                    .orderBy("name", Query.Direction.ASCENDING) 
            }
            
            val snapshot = query.get().await()
            val animations = snapshot.documents.mapNotNull { 
                it.toObject(Animation::class.java) 
            }
            
            emit(Result.success(animations))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting animations", e)
            emit(Result.failure(e))
        }
    }
    
    /**
     * Save an animation for a model
     * @param modelId The ID of the model
     * @param animationId The ID of the animation to apply
     * @param name Optional name for the saved animation
     * @return Result with success if saved, failure otherwise
     */
    suspend fun saveAnimation(modelId: String, animationId: String, name: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            
            // Create a saved animation document
            val savedAnimation = hashMapOf(
                "userId" to userId,
                "modelId" to modelId,
                "animationId" to animationId,
                "name" to name,
                "createdAt" to System.currentTimeMillis()
            )
            
            // Add to saved animations collection
            modelsCollection.document(modelId)
                .collection("savedAnimations")
                .add(savedAnimation)
                .await()
                
            // Update the model to indicate it has animations
            modelsCollection.document(modelId)
                .update("hasAnimation", true)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving animation", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get user data
     * @return Flow emitting Result with User data
     */
    fun getUserData(): Flow<Result<User>> = flow {
        try {
            val userId = getCurrentUserId() ?: throw Exception("User not logged in")
            
            val document = usersCollection.document(userId).get().await()
            
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    emit(Result.success(user))
                } else {
                    emit(Result.failure(Exception("Failed to convert document to User")))
                }
            } else {
                emit(Result.failure(Exception("User not found")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user data", e)
            emit(Result.failure(e))
        }
    }
    
    /**
     * Get user models
     * @return Flow emitting Result with list of user's models
     */
    fun getUserModels(): Flow<Result<List<Model3D>>> = flow {
        try {
            val userId = getCurrentUserId() ?: throw Exception("User not logged in")
            
            val query = modelsCollection
                .whereEqualTo("userId", userId)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                
            val snapshot = query.get().await()
            val models = snapshot.documents.mapNotNull { 
                it.toObject(Model3D::class.java) 
            }
            
            emit(Result.success(models))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user models", e)
            emit(Result.failure(e))
        }
    }
    
    /**
     * Get recent models (public or owned by the user)
     * @param limit Optional limit on the number of models to retrieve
     * @return Flow emitting Result with list of recent models
     */
    fun getRecentModels(limit: Int = 10): Flow<Result<List<Model3D>>> = flow {
        try {
            val userId = getCurrentUserId()
            
            // Query for recent models (either public or owned by the user)
            val query = if (userId != null) {
                modelsCollection
                    .whereEqualTo("isPublic", true)
                    .orderBy("creationDate", Query.Direction.DESCENDING)
                    .limit(limit.toLong())
            } else {
                modelsCollection
                    .whereEqualTo("isPublic", true)
                    .orderBy("creationDate", Query.Direction.DESCENDING)
                    .limit(limit.toLong())
            }
            
            val snapshot = query.get().await()
            val models = snapshot.documents.mapNotNull { 
                it.toObject(Model3D::class.java) 
            }
            
            emit(Result.success(models))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recent models", e)
            emit(Result.failure(e))
        }
    }
    
    /**
     * Upload a source image for model creation
     * @param modelId The ID of the model being created
     * @param imageUri The URI of the image to upload
     * @return Result with the download URL if successful
     */
    suspend fun uploadSourceImage(modelId: String, imageUri: Uri): Result<String> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            
            // Create a storage reference
            val storageRef = storage.reference
                .child("users")
                .child(userId)
                .child("models")
                .child(modelId)
                .child("source.jpg")
                
            // Upload the file
            val uploadTask = storageRef.putFile(imageUri).await()
            
            // Get the download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            // Update the model with the source image URL
            modelsCollection.document(modelId)
                .update("sourceImageUrl", downloadUrl)
                .await()
                
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading source image", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sign in with email and password
     * @param email The user's email
     * @param password The user's password
     * @return Result with FirebaseUser if successful, or an exception if failed
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Authentication failed")
            
            // Check if user document exists, create if not
            val userDoc = usersCollection.document(user.uid).get().await()
            if (!userDoc.exists()) {
                createUserDocument(user)
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error signing in", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sign up with email and password
     * @param email The user's email
     * @param password The user's password
     * @param displayName The user's display name
     * @return Result with FirebaseUser if successful, or an exception if failed
     */
    suspend fun signUp(email: String, password: String, displayName: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User creation failed")
            
            // Update display name
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profileUpdates).await()
            
            // Create user document in Firestore
            createUserDocument(user, displayName)
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error signing up", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sign out the current user
     */
    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Reset password for an email address
     * @param email The email address to reset password for
     * @return Result with success if email sent, failure otherwise
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting password", e)
            Result.failure(e)
        }
    }
    
    /**
     * Check if a user is currently logged in
     * @return True if logged in, false otherwise
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Update user settings
     * @param settings The new user settings
     * @return Result with success if updated, failure otherwise
     */
    suspend fun updateUserSettings(settings: UserSettings): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            
            usersCollection.document(userId)
                .update("settings", settings)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user settings", e)
            Result.failure(e)
        }
    }
    
    /**
     * Create a user document in Firestore
     * @param user The FirebaseUser
     * @param displayName Optional display name
     */
    private suspend fun createUserDocument(user: FirebaseUser, displayName: String? = null) {
        try {
            val userId = user.uid
            val userEmail = user.email ?: ""
            val userName = displayName ?: user.displayName ?: userEmail.substringBefore("@")
            
            val userData = User(
                id = userId,
                email = userEmail,
                displayName = userName,
                photoUrl = user.photoUrl?.toString(),
                subscription = SubscriptionType.FREE_TRIAL
            )
            
            usersCollection.document(userId)
                .set(userData)
                .await()
                
            Log.d(TAG, "User document created for $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user document", e)
        }
    }
}