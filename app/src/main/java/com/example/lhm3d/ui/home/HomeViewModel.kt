package com.example.lhm3d.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lhm3d.data.model.Model

class HomeViewModel : ViewModel() {

    private val _featuredModels = MutableLiveData<List<Model>>()
    val featuredModels: LiveData<List<Model>> = _featuredModels

    private val _recentModels = MutableLiveData<List<Model>>()
    val recentModels: LiveData<List<Model>> = _recentModels

    init {
        // Fetch data from repository
        fetchFeaturedModels()
        fetchRecentModels()
    }

    private fun fetchFeaturedModels() {
        // In a real app, this would fetch from a repository
        // For now, we'll add placeholder initialization
        _featuredModels.value = listOf()
    }

    private fun fetchRecentModels() {
        // In a real app, this would fetch from a repository
        // For now, we'll add placeholder initialization
        _recentModels.value = listOf()
    }
}