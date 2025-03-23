package com.example.lhm3d.ui.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.data.repository.ModelRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CreateViewModel(private val context: Context) : ViewModel() {
    
    private val modelRepository = ModelRepository(context)
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _createResult = MutableLiveData<Result<String>>()
    val createResult: LiveData<Result<String>> = _createResult
    
    /**
     * Create a new 3D model from the provided image
     */
    fun createModel(name: String, description: String, imageUri: Uri, isPublic: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                modelRepository.createModelFromImage(name, description, imageUri, isPublic)
                    .collect { modelId ->
                        if (modelId.isNotEmpty()) {
                            _createResult.value = Result.success(modelId)
                        } else {
                            _createResult.value = Result.failure(Exception("Failed to create model"))
                        }
                    }
            } catch (e: Exception) {
                _createResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}