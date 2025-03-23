package com.example.lhm3d.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.ProcessingStatus
import com.example.lhm3d.service.FirebaseService
import com.example.lhm3d.service.ModelService
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

/**
 * ViewModel for the preview screen.
 */
class PreviewViewModel(application: Application) : AndroidViewModel(application) {
    
    private val firebaseService = FirebaseService()
    private val modelService = ModelService(application.applicationContext)
    
    // Current model
    private val _currentModel = MutableLiveData<Result<Model3D>?>()
    val currentModel: LiveData<Result<Model3D>?> = _currentModel
    
    // Processing state
    private val _processingState = MutableLiveData<ProcessingStatus>()
    val processingState: LiveData<ProcessingStatus> = _processingState

    /**
     * Process an image to create a 3D model.
     */
    fun processImage(imageUri: Uri) {
        _processingState.value = ProcessingStatus.PROCESSING
        
        viewModelScope.launch {
            try {
                // Generate a unique ID for the model
                val modelId = UUID.randomUUID().toString()
                
                // Upload the image to Firebase
                val uploadResult = firebaseService.uploadSourceImage(modelId, imageUri)
                
                if (uploadResult.isSuccess) {
                    val downloadUrl = uploadResult.getOrThrow()
                    
                    // Create a basic model with the source image URL
                    val model = Model3D(
                        id = modelId,
                        sourceImageUrl = downloadUrl,
                        processingStatus = ProcessingStatus.PROCESSING
                    )
                    
                    // Process the image
                    val processResult = modelService.processImage(model)
                    
                    if (processResult.isSuccess) {
                        val processedModel = processResult.getOrThrow()
                        _currentModel.value = Result.success(processedModel)
                        _processingState.value = ProcessingStatus.COMPLETED
                    } else {
                        _currentModel.value = Result.failure(
                            processResult.exceptionOrNull() ?: Exception("Processing failed")
                        )
                        _processingState.value = ProcessingStatus.FAILED
                    }
                } else {
                    _currentModel.value = Result.failure(
                        uploadResult.exceptionOrNull() ?: Exception("Upload failed")
                    )
                    _processingState.value = ProcessingStatus.FAILED
                }
            } catch (e: Exception) {
                _currentModel.value = Result.failure(e)
                _processingState.value = ProcessingStatus.FAILED
            }
        }
    }

    /**
     * Load an existing model.
     */
    fun loadModel(modelId: String) {
        _processingState.value = ProcessingStatus.PROCESSING
        
        viewModelScope.launch {
            val result = firebaseService.getModel(modelId)
            
            if (result.isSuccess) {
                val model = result.getOrThrow()
                _currentModel.value = Result.success(model)
                _processingState.value = ProcessingStatus.COMPLETED
            } else {
                _currentModel.value = Result.failure(
                    result.exceptionOrNull() ?: Exception("Model not found")
                )
                _processingState.value = ProcessingStatus.FAILED
            }
        }
    }
}
