package com.example.lhm3d.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.lhm3d.R
import com.example.lhm3d.data.model.Model3D
import com.example.lhm3d.data.model.ProcessingStatus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Manager class for all Firebase operations including authentication, storage, and Firestore
 */
class FirebaseManager private constructor(private val context: Context) {

    // Firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    // Google Sign In
    private val googleSignInClient: GoogleSignInClient
    
    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Web client ID from google-services.json
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    // Collection references
    private val modelsCollection = firestore.collection("models")
    private val usersCollection = firestore.collection("users")
    
    companion object {
        @Volatile
        private var instance: FirebaseManager? = null
        
        fun getInstance(context: Context): FirebaseManager {
            return instance ?: synchronized(this) {
                instance ?: FirebaseManager(context).also { instance = it }
            }
        }
        
        fun getInstance(): FirebaseManager {
            return instance ?: throw IllegalStateException("FirebaseManager must be initialized with a context first")
        }
    }
    
    fun getContext(): Context {
        return context
    }
    
    /**
     * Auth methods
     */
    
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    
    fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent
    
    suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).await()
        
        // Save or update user profile in Firestore
        val user = auth.currentUser
        if (user != null) {
            val userData = hashMapOf(
                "uid" to user.uid,
                "displayName" to user.displayName,
                "email" to user.email,
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "lastLogin" to System.currentTimeMillis()
            )
            usersCollection.document(user.uid).set(userData).await()
        }
    }
    
    fun handleSignInResult(data: Intent?): GoogleSignInAccount? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            null
        }
    }
    
    suspend fun signOut() {
        googleSignInClient.signOut().await()
        auth.signOut()
    }
    
    /**
     * Firestore model methods
     */
    
    suspend fun getUserModels(): List<Model3D> {
        val currentUser = getCurrentUser() ?: return emptyList()
        
        return modelsCollection
            .whereEqualTo("userId", currentUser.uid)
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Model3D::class.java)
    }
    
    suspend fun getPublicModels(): List<Model3D> {
        return modelsCollection
            .whereEqualTo("isPublic", true)
            .whereEqualTo("status", ProcessingStatus.COMPLETED)
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .await()
            .toObjects(Model3D::class.java)
    }
    
    suspend fun getModelById(modelId: String): Model3D? {
        val document = modelsCollection.document(modelId).get().await()
        return document.toObject(Model3D::class.java)
    }
    
    suspend fun saveModel(model: Model3D): String {
        val modelId = model.id.ifEmpty { UUID.randomUUID().toString() }
        val modelWithId = model.copy(id = modelId)
        
        modelsCollection.document(modelId).set(modelWithId).await()
        return modelId
    }
    
    suspend fun deleteModel(modelId: String) {
        // Delete the model from Firestore
        modelsCollection.document(modelId).delete().await()
        
        // Delete associated files from Storage
        val modelRef = storage.reference.child("models/$modelId")
        modelRef.listAll().await().items.forEach { it.delete().await() }
    }
    
    /**
     * Storage methods
     */
    
    suspend fun uploadSourceImage(modelId: String, imageUri: Uri): String {
        val fileName = "source_image.jpg"
        val storageRef = storage.reference.child("models/$modelId/$fileName")
        
        storageRef.putFile(imageUri).await()
        return storageRef.downloadUrl.await().toString()
    }
    
    suspend fun uploadModelFile(modelId: String, modelFileUri: Uri): String {
        val fileName = "model.obj"
        val storageRef = storage.reference.child("models/$modelId/$fileName")
        
        storageRef.putFile(modelFileUri).await()
        return storageRef.downloadUrl.await().toString()
    }
}