package com.example.lhm3d.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.data.model.Model
import com.example.lhm3d.data.repository.ModelRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val repository = ModelRepository()
    
    private val _featuredModels = MutableLiveData<List<Model>>()
    val featuredModels: LiveData<List<Model>> = _featuredModels
    
    private val _recentModels = MutableLiveData<List<Model>>()
    val recentModels: LiveData<List<Model>> = _recentModels
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        loadHomeData()
    }
    
    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Load community models
                repository.fetchCommunityModels()
                
                // Load user's models if authenticated
                repository.fetchUserModels()
                
                // For featured models, we're just using community models for now
                // In a real app, this would be curated
                _featuredModels.value = repository.communityModels.value
                    ?.filter { it.isPublic }
                    ?.sortedByDescending { it.likes }
                    ?.take(5)
                    ?: emptyList()
                
                // For recent models, get user's most recent models
                _recentModels.value = repository.userModels.value
                    ?.sortedByDescending { it.createdAt }
                    ?.take(5)
                    ?: emptyList()
                
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshData() {
        loadHomeData()
    }
}