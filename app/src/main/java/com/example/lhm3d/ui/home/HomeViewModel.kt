package com.example.lhm3d.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.data.model.Model3D
import com.example.lhm3d.data.repository.ModelRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen
 */
class HomeViewModel : ViewModel() {
    
    private val modelRepository = ModelRepository()
    
    // LiveData for UI states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _recentModels = MutableLiveData<List<Model3D>>()
    val recentModels: LiveData<List<Model3D>> = _recentModels
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Load recent models from the repository
     */
    fun loadRecentModels() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val models = modelRepository.getRecentModels(10) // Limit to 10 recent models
                _recentModels.value = models
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refresh the model list, for use with swipe refresh
     */
    fun refreshModels() {
        loadRecentModels()
    }
}