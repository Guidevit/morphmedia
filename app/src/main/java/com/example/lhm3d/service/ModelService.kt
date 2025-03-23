package com.example.lhm3d.service

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.ProcessingStatus
import com.example.lhm3d.repository.ModelsRepository
import com.example.lhm3d.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Service for handling 3D model processing using the LHM model.
 */
class ModelService(private val context: Context) {
    private val TAG = "ModelService"
    private val modelsRepository = ModelsRepository()
    
    // Cloud API URL - this would be your Firebase Cloud Function or other backend service
    private val API_URL = "https://your-firebase-cloud-function-url.com/process_image"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS) // Models can take time to process
        .build()
    
    /**
     * Process an image to create a 3D model.
     * This would call a backend API that runs the LHM model.
     */
    suspend fun processImage(model: Model3D): Result<Model3D> = withContext(Dispatchers.IO) {
        try {
            // Update model status to PROCESSING
            modelsRepository.updateModelStatus(model.id, ProcessingStatus.PROCESSING)
            
            // Download the source image from Firebase
            val imageUri = Uri.parse(model.sourceImageUrl)
            val imageFile = downloadImage(imageUri)
            
            // Process the image using the API
            val result = sendImageToAPI(imageFile, model.id)
            if (result.isSuccess) {
                val modelData = result.getOrThrow()
                
                // Create a thumbnail from the input image
                val bitmap = ImageUtils.getBitmapFromUri(context, imageUri)
                val thumbnailFile = File(context.cacheDir, "${model.id}_thumbnail.jpg")
                val thumbnailUri = saveBitmapToFile(bitmap, thumbnailFile)
                
                // Save the processed model data
                val savedModel = modelsRepository.saveModelData(
                    model.id,
                    modelData,
                    thumbnailUri
                ).getOrThrow()
                
                // Cleanup temporary files
                imageFile.delete()
                modelData.delete()
                
                return@withContext Result.success(savedModel)
            } else {
                modelsRepository.updateModelStatus(
                    model.id,
                    ProcessingStatus.FAILED,
                    result.exceptionOrNull()?.message ?: "Unknown error"
                )
                return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image", e)
            modelsRepository.updateModelStatus(
                model.id,
                ProcessingStatus.FAILED,
                e.message
            )
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Download an image from a URI.
     */
    private suspend fun downloadImage(imageUri: Uri): File = withContext(Dispatchers.IO) {
        val bitmap = ImageUtils.getBitmapFromUri(context, imageUri)
        val file = File(context.cacheDir, "input_image.jpg")
        saveBitmapToFile(bitmap, file)
        return@withContext file
    }
    
    /**
     * Save a bitmap to a file.
     */
    private fun saveBitmapToFile(bitmap: Bitmap, file: File): Uri {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return Uri.fromFile(file)
    }
    
    /**
     * Send the image to the API for processing.
     * This is a placeholder for the actual API call.
     */
    private suspend fun sendImageToAPI(imageFile: File, modelId: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            // In a real app, this would send the image to a Cloud Function or backend service
            // that runs the LHM model and returns the processed 3D model
            
            // For simulation, we'll create a mock response
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "image",
                    imageFile.name,
                    imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                .addFormDataPart("model_id", modelId)
                .build()
            
            val request = Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .build()
            
            // This would be a real API call in production
            // client.newCall(request).execute().use { response ->
            //     if (!response.isSuccessful) throw IOException("API call failed: ${response.code}")
            //     
            //     // Parse response and download model file
            //     val responseBody = response.body?.string() ?: throw IOException("Empty response")
            //     val json = JSONObject(responseBody)
            //     val modelUrl = json.getString("model_url")
            //     
            //     // Download model file
            //     return@withContext downloadModelFile(modelUrl, modelId)
            // }
            
            // Simulated response - in reality, this would be the model from LHM
            val outputFile = File(context.cacheDir, "${modelId}.glb")
            
            // Create a simple placeholder file for demo
            FileOutputStream(outputFile).use { out ->
                out.write("Placeholder for 3D model data".toByteArray())
            }
            
            return@withContext Result.success(outputFile)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending image to API", e)
            return@withContext Result.failure(e)
        }
    }
    
    /**
     * Download a model file from a URL.
     */
    private suspend fun downloadModelFile(modelUrl: String, modelId: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(modelUrl).build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Download failed: ${response.code}")
                
                val outputFile = File(context.cacheDir, "${modelId}.glb")
                response.body?.byteStream()?.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
                
                return@withContext Result.success(outputFile)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading model file", e)
            return@withContext Result.failure(e)
        }
    }
}
