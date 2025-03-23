package com.example.lhm3d.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lhm3d.data.model.Model

class GalleryViewModel : ViewModel() {

    private val _models = MutableLiveData<List<Model>>()
    val models: LiveData<List<Model>> = _models
    
    private var isShowingMyModels = true

    init {
        // Initialize with the user's models
        fetchMyModels()
    }
    
    fun showMyModels() {
        if (!isShowingMyModels) {
            isShowingMyModels = true
            fetchMyModels()
        }
    }
    
    fun showCommunityModels() {
        if (isShowingMyModels) {
            isShowingMyModels = false
            fetchCommunityModels()
        }
    }
    
    private fun fetchMyModels() {
        // In a real app, this would fetch from a repository
        // For now, we'll initialize with an empty list
        _models.value = listOf()
    }
    
    private fun fetchCommunityModels() {
        // In a real app, this would fetch from a repository
        // For now, we'll initialize with an empty list
        _models.value = listOf()
    }
}