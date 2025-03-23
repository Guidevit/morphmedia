package com.example.lhm3d.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.service.FirebaseService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel for the home screen.
 */
class HomeViewModel : ViewModel() {
    
    private val firebaseService = FirebaseService()
    
    // Recent models
    private val _recentModels = MutableLiveData<Result<List<Model3D>>?>()
    val recentModels: LiveData<Result<List<Model3D>>?> = _recentModels
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Load recent models for the user.
     */
    fun loadRecentModels() {
        _isLoading.value = true
        viewModelScope.launch {
            firebaseService.getRecentModels().collect { result ->
                _recentModels.value = result
                _isLoading.value = false
            }
        }
    }
}
