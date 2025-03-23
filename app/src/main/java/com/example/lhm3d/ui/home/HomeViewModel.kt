package com.example.lhm3d.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.data.model.Model3D
import com.example.lhm3d.data.repository.ModelRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val modelRepository = ModelRepository()
    
    private val _recentModels = MutableLiveData<List<Model3D>>()
    val recentModels: LiveData<List<Model3D>> = _recentModels
    
    private val _popularModels = MutableLiveData<List<Model3D>>()
    val popularModels: LiveData<List<Model3D>> = _popularModels
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        loadRecentModels()
        loadPopularModels()
    }
    
    private fun loadRecentModels() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val models = modelRepository.getRecentModels(5)
                _recentModels.value = models
            } catch (e: Exception) {
                // Handle error 
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadPopularModels() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val models = modelRepository.getPopularModels(5)
                _popularModels.value = models
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}