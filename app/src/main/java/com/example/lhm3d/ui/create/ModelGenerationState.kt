package com.example.lhm3d.ui.create

sealed class ModelGenerationState {
    data class Processing(val progress: Int) : ModelGenerationState()
    data class Success(val modelId: String) : ModelGenerationState()
    data class Error(val message: String) : ModelGenerationState()
}