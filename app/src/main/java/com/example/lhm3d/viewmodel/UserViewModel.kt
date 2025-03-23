package com.example.lhm3d.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.data.repository.FirebaseManager
import com.example.lhm3d.model.SubscriptionType
import com.example.lhm3d.model.User
import com.example.lhm3d.model.UserSettings
import com.example.lhm3d.repository.UserRepository
import com.example.lhm3d.service.FirebaseService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel for user-related operations.
 */
class UserViewModel : ViewModel() {
    
    private val firebaseService = FirebaseService()
    private val userRepository = UserRepository()
    private lateinit var googleSignInClient: GoogleSignInClient
    
    // Authentication state
    private val _authState = MutableLiveData<Result<Unit>?>()
    val authState: LiveData<Result<Unit>?> = _authState
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Password reset state
    private val _passwordResetState = MutableLiveData<Result<Unit>?>()
    val passwordResetState: LiveData<Result<Unit>?> = _passwordResetState
    
    // User data
    private val _userData = MutableLiveData<Result<User>?>()
    val userData: LiveData<Result<User>?> = _userData
    
    // Settings update result
    private val _settingsUpdateResult = MutableLiveData<Result<Unit>?>()
    val settingsUpdateResult: LiveData<Result<Unit>?> = _settingsUpdateResult

    /**
     * Sign in with email and password.
     */
    fun signIn(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = firebaseService.signIn(email, password)
            if (result.isSuccess) {
                _authState.value = Result.success(Unit)
            } else {
                _authState.value = Result.failure(result.exceptionOrNull() ?: Exception("Sign in failed"))
            }
            _isLoading.value = false
        }
    }

    /**
     * Sign up with email and password.
     */
    fun signUp(email: String, password: String, displayName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = firebaseService.signUp(email, password, displayName)
            if (result.isSuccess) {
                _authState.value = Result.success(Unit)
            } else {
                _authState.value = Result.failure(result.exceptionOrNull() ?: Exception("Sign up failed"))
            }
            _isLoading.value = false
        }
    }

    /**
     * Sign out the current user.
     */
    fun signOut() {
        firebaseService.signOut()
    }

    /**
     * Reset password for a user.
     */
    fun resetPassword(email: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = firebaseService.resetPassword(email)
            _passwordResetState.value = result
            _isLoading.value = false
        }
    }

    /**
     * Check if a user is logged in.
     */
    fun isUserLoggedIn(): Boolean {
        return firebaseService.isUserLoggedIn()
    }

    /**
     * Load user data.
     */
    fun loadUserData() {
        viewModelScope.launch {
            firebaseService.getUserData().collect { result ->
                _userData.value = result
            }
        }
    }

    /**
     * Update user settings.
     */
    fun updateUserSettings(settings: UserSettings) {
        viewModelScope.launch {
            val result = firebaseService.updateUserSettings(settings)
            _settingsUpdateResult.value = result
        }
    }

    /**
     * Update user subscription.
     */
    fun updateSubscription(subscriptionType: SubscriptionType) {
        viewModelScope.launch {
            firebaseService.updateSubscription(subscriptionType)
            // Reload user data to get updated subscription
            loadUserData()
        }
    }

    /**
     * Clear the password reset state.
     */
    fun clearPasswordResetState() {
        _passwordResetState.value = null
    }

    /**
     * Clear the settings update result.
     */
    fun clearSettingsUpdateResult() {
        _settingsUpdateResult.value = null
    }
    
    /**
     * Get Google Sign-In Intent.
     */
    fun getGoogleSignInIntent(): Intent {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("web_client_id") // Replace with your actual web client ID
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(FirebaseManager.getInstance().getContext(), gso)
        return googleSignInClient.signInIntent
    }
    
    /**
     * Handle Google Sign-In result.
     */
    fun handleGoogleSignInResult(data: Intent?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(Exception::class.java)
                val idToken = account.idToken
                
                // Firebase auth with Google
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = userRepository.signInWithCredential(credential)
                
                _authState.value = Result.success(Unit)
            } catch (e: Exception) {
                _authState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
