package com.example.lhm3d.data.remote

import android.net.Uri
import com.example.lhm3d.data.model.Model3D
import com.example.lhm3d.data.model.ProcessingStatus
import com.example.lhm3d.data.repository.FirebaseManager
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

/**
 * Service class that interfaces with the LHM Cloud Functions on Firebase
 */
class LhmApiService(private val firebaseManager: FirebaseManager) {

    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
    
    /**
     * Submits an image for 3D model generation
     *
     * @param modelId The ID of the model to process
     * @param imageUrl The URL of the uploaded image
     * @return Result containing the model ID if successful or an error
     */
    suspend fun submitModelGeneration(modelId: String, imageUrl: String): Result<String> {
        return try {
            val data = hashMapOf(
                "modelId" to modelId,
                "imageUrl" to imageUrl,
                "userId" to (firebaseManager.getCurrentUser()?.uid ?: "")
            )
            
            // Call the Firebase Cloud Function
            val result = functions
                .getHttpsCallable("generateModel")
                .call(data)
                .await()
                .data as Map<*, *>
            
            val success = result["success"] as? Boolean ?: false
            if (success) {
                val resultModelId = result["modelId"] as? String ?: ""
                Result.success(resultModelId)
            } else {
                val errorMsg = result["error"] as? String ?: "Unknown error during model generation"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Applies animation to an existing 3D model
     *
     * @param modelId The ID of the model to animate
     * @param animationData Animation parameters
     * @return Result containing the updated model if successful or an error
     */
    suspend fun animateModel(modelId: String, animationData: Map<String, Any>): Result<Model3D?> {
        return try {
            val data = hashMapOf(
                "modelId" to modelId,
                "userId" to (firebaseManager.getCurrentUser()?.uid ?: ""),
                "animation" to animationData
            )
            
            // Call the Firebase Cloud Function
            val result = functions
                .getHttpsCallable("animateModel")
                .call(data)
                .await()
                .data as Map<*, *>
            
            val success = result["success"] as? Boolean ?: false
            if (success) {
                // Get the updated model from Firebase
                val updatedModel = firebaseManager.getModelById(modelId)
                
                Result.success(updatedModel)
            } else {
                val errorMsg = result["error"] as? String ?: "Unknown error during animation"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Checks the processing status of a model
     *
     * @param modelId The ID of the model to check
     * @return The current processing status of the model
     */
    suspend fun checkModelStatus(modelId: String): ProcessingStatus {
        return try {
            val model = firebaseManager.getModelById(modelId)
            model?.status ?: ProcessingStatus.PENDING
        } catch (e: Exception) {
            ProcessingStatus.FAILED
        }
    }
}