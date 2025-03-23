package com.example.lhm3d.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class CreateViewModel : ViewModel() {

    private val _modelGenerationState = MutableLiveData<ModelGenerationState>()
    val modelGenerationState: LiveData<ModelGenerationState> = _modelGenerationState

    fun generateModel(imageUri: String) {
        viewModelScope.launch {
            _modelGenerationState.value = ModelGenerationState.Processing(0)
            
            // Simulate model generation process
            for (i in 1..5) {
                delay(1000) // 1 second delay to simulate processing
                _modelGenerationState.value = ModelGenerationState.Processing(i * 20)
            }
            
            // In a real app, this would call a repository to generate the model
            // For now, we'll simulate success with a random UUID
            val modelId = UUID.randomUUID().toString()
            _modelGenerationState.value = ModelGenerationState.Success(modelId)
        }
    }
}