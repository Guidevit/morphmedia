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
 * ViewModel for the gallery screen.
 */
class GalleryViewModel : ViewModel() {
    
    private val firebaseService = FirebaseService()
    
    // All models
    private val _models = MutableLiveData<Result<List<Model3D>>?>()
    val models: LiveData<Result<List<Model3D>>?> = _models
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Current list of models (for sorting)
    private var currentModels: List<Model3D> = emptyList()

    /**
     * Load all models for the user.
     */
    fun loadModels() {
        _isLoading.value = true
        viewModelScope.launch {
            firebaseService.getUserModels().collect { result ->
                _models.value = result
                _isLoading.value = false
                
                // Store the current models for sorting
                result.getOrNull()?.let {
                    currentModels = it
                }
            }
        }
    }

    /**
     * Sort models by newest first.
     */
    fun sortByNewest() {
        val sorted = currentModels.sortedByDescending { it.createdAt }
        _models.value = Result.success(sorted)
    }

    /**
     * Sort models by oldest first.
     */
    fun sortByOldest() {
        val sorted = currentModels.sortedBy { it.createdAt }
        _models.value = Result.success(sorted)
    }

    /**
     * Sort models by name (A-Z).
     */
    fun sortByNameAsc() {
        val sorted = currentModels.sortedBy { it.name }
        _models.value = Result.success(sorted)
    }

    /**
     * Sort models by name (Z-A).
     */
    fun sortByNameDesc() {
        val sorted = currentModels.sortedByDescending { it.name }
        _models.value = Result.success(sorted)
    }
}
