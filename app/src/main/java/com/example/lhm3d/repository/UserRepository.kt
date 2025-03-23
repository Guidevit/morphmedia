package com.example.lhm3d.repository

import com.example.lhm3d.model.SubscriptionType
import com.example.lhm3d.model.User
import com.example.lhm3d.model.UserSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repository for managing user data.
 */
class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    /**
     * Get the current logged-in Firebase user.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Sign in with email and password.
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                return@withContext Result.success(it)
            } ?: return@withContext Result.failure(Exception("Authentication failed"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Sign up with email and password.
     */
    suspend fun signUp(email: String, password: String, displayName: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let { firebaseUser ->
                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    displayName = displayName,
                    subscription = SubscriptionType.FREE_TRIAL,
                    remainingCredits = 5
                )
                usersCollection.document(firebaseUser.uid).set(user).await()
                return@withContext Result.success(firebaseUser)
            } ?: return@withContext Result.failure(Exception("User creation failed"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Sign out the current user.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Get user data as a Flow.
     */
    fun getUserData(userId: String): Flow<Result<User>> = flow {
        try {
            val documentSnapshot = usersCollection.document(userId).get().await()
            val user = documentSnapshot.toObject(User::class.java)
            if (user != null) {
                emit(Result.success(user))
            } else {
                emit(Result.failure(Exception("User not found")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Update user settings.
     */
    suspend fun updateUserSettings(userId: String, settings: UserSettings): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(userId).update("settings", settings).await()
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Update user subscription.
     */
    suspend fun updateSubscription(userId: String, subscriptionType: SubscriptionType): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(userId).update("subscription", subscriptionType).await()
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Decrease the remaining credits for free trial users.
     */
    suspend fun decreaseCredits(userId: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val documentSnapshot = usersCollection.document(userId).get().await()
            val user = documentSnapshot.toObject(User::class.java)
            if (user != null) {
                if (user.subscription == SubscriptionType.FREE_TRIAL && user.remainingCredits > 0) {
                    val newCredits = user.remainingCredits - 1
                    usersCollection.document(userId).update("remainingCredits", newCredits).await()
                    return@withContext Result.success(newCredits)
                } else if (user.subscription != SubscriptionType.FREE_TRIAL) {
                    return@withContext Result.success(-1) // -1 means unlimited
                } else {
                    return@withContext Result.failure(Exception("No credits remaining"))
                }
            } else {
                return@withContext Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Reset password for a user.
     */
    suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email).await()
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Sign in with credentials (Google, Facebook, etc.).
     */
    suspend fun signInWithCredential(credential: com.google.firebase.auth.AuthCredential): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInWithCredential(credential).await()
            authResult.user?.let { firebaseUser ->
                // Check if this is a new user or existing user
                val userDoc = usersCollection.document(firebaseUser.uid).get().await()
                if (!userDoc.exists()) {
                    // Create a new user document for first-time Google sign-in
                    val user = User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "User",
                        subscription = SubscriptionType.FREE_TRIAL,
                        remainingCredits = 5
                    )
                    usersCollection.document(firebaseUser.uid).set(user).await()
                }
                return@withContext Result.success(firebaseUser)
            } ?: return@withContext Result.failure(Exception("Authentication failed"))
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}
