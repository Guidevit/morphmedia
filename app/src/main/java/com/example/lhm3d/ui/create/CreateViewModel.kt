package com.example.lhm3d.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.data.model.Model3D
import com.example.lhm3d.data.repository.ModelRepository
import kotlinx.coroutines.launch
import java.io.File

class CreateViewModel : ViewModel() {
    
    private val modelRepository = ModelRepository()
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _createResult = MutableLiveData<Result<Model3D>>()
    val createResult: LiveData<Result<Model3D>> = _createResult
    
    /**
     * Create a new 3D model from the provided image
     */
    fun createModel(name: String, description: String, imageFile: File, isPublic: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val model = modelRepository.createModelFromImage(name, description, imageFile, isPublic)
                model?.let {
                    _createResult.value = Result.success(it)
                } ?: run {
                    _createResult.value = Result.failure(Exception("Failed to create model"))
                }
            } catch (e: Exception) {
                _createResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}