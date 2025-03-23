package com.example.lhm3d.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.model.SubscriptionType
import com.example.lhm3d.model.User
import com.example.lhm3d.model.UserSettings
import com.example.lhm3d.service.FirebaseService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel for user-related operations.
 */
class UserViewModel : ViewModel() {
    
    private val firebaseService = FirebaseService()
    
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
            _authState.value = result
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
            _authState.value = result
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
            val result = firebaseService.updateSubscription(subscriptionType)
            if (result.isSuccess) {
                // Reload user data to get updated subscription
                loadUserData()
            }
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
}
