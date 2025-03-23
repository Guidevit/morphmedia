package com.example.lhm3d.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lhm3d.model.Animation
import com.example.lhm3d.model.AnimationSettings
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.SavedAnimation
import com.example.lhm3d.service.FirebaseService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel for the animation screen.
 */
class AnimationViewModel : ViewModel() {
    
    private val firebaseService = FirebaseService()
    
    // Model
    private val _model = MutableLiveData<Result<Model3D>?>()
    val model: LiveData<Result<Model3D>?> = _model
    
    // Available animations
    private val _animations = MutableLiveData<Result<List<Animation>>?>()
    val animations: LiveData<Result<List<Animation>>?> = _animations
    
    // Selected animation
    private val _selectedAnimation = MutableLiveData<Animation?>()
    val selectedAnimation: LiveData<Animation?> = _selectedAnimation
    
    // Save result
    private val _saveResult = MutableLiveData<Result<SavedAnimation>?>()
    val saveResult: LiveData<Result<SavedAnimation>?> = _saveResult
    
    // Current model ID
    private var currentModelId: String? = null
    
    // Current animation list
    private var animationList: List<Animation> = emptyList()

    /**
     * Load model data.
     */
    fun loadModel(modelId: String) {
        currentModelId = modelId
        viewModelScope.launch {
            val result = firebaseService.getModel(modelId)
            _model.value = result
        }
    }

    /**
     * Load available animations.
     */
    fun loadAnimations() {
        viewModelScope.launch {
            firebaseService.getAvailableAnimations().collect { result ->
                _animations.value = result
                
                // Store the animation list
                result.getOrNull()?.let {
                    animationList = it
                }
            }
        }
    }

    /**
     * Select an animation by name.
     */
    fun selectAnimation(animationName: String) {
        val animation = animationList.find { it.name == animationName }
        _selectedAnimation.value = animation
    }

    /**
     * Play the current animation.
     */
    fun playAnimation() {
        // In a real app, this would communicate with the renderer
    }

    /**
     * Pause the current animation.
     */
    fun pauseAnimation() {
        // In a real app, this would communicate with the renderer
    }

    /**
     * Reset the current animation.
     */
    fun resetAnimation() {
        // In a real app, this would communicate with the renderer
    }

    /**
     * Set animation looping.
     */
    fun setLooping(loop: Boolean) {
        // In a real app, this would communicate with the renderer
    }

    /**
     * Set animation speed.
     */
    fun setSpeed(speed: Float) {
        // In a real app, this would communicate with the renderer
    }

    /**
     * Save the current animation with settings.
     */
    fun saveAnimation(settings: AnimationSettings) {
        val modelId = currentModelId ?: return
        val animation = _selectedAnimation.value ?: return
        
        viewModelScope.launch {
            try {
                firebaseService.saveAnimation(
                    modelId = modelId,
                    animationId = animation.id,
                    name = "${animation.name} ${System.currentTimeMillis()}"
                )
                
                // For simplicity, we're creating a success result
                val savedAnimation = SavedAnimation(
                    modelId = modelId,
                    animationId = animation.id,
                    name = animation.name,
                    settings = settings
                )
                _saveResult.value = Result.success(savedAnimation)
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }

    /**
     * Clear the save result.
     */
    fun clearSaveResult() {
        _saveResult.value = null
    }
}
