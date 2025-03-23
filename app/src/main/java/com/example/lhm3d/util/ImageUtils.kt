package com.example.lhm3d.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Utility functions for image processing.
 */
object ImageUtils {
    
    private const val TAG = "ImageUtils"
    private const val MAX_IMAGE_DIMENSION = 1024
    
    /**
     * Get a bitmap from a URI.
     */
    suspend fun getBitmapFromUri(context: Context, uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            // Rotate the bitmap if needed
            val rotatedBitmap = rotateBitmapIfNeeded(context, uri, bitmap)
            
            // Resize the bitmap if needed
            val resizedBitmap = resizeBitmapIfNeeded(rotatedBitmap)
            
            return@withContext resizedBitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from URI", e)
            throw e
        }
    }
    
    /**
     * Rotate a bitmap based on EXIF orientation.
     */
    private fun rotateBitmapIfNeeded(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = ExifInterface(inputStream)
            inputStream.close()
            
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            
            val matrix = Matrix()
            
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                else -> return bitmap
            }
            
            return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error rotating bitmap", e)
            return bitmap
        }
    }
    
    /**
     * Resize a bitmap if it's too large.
     */
    private fun resizeBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= MAX_IMAGE_DIMENSION && height <= MAX_IMAGE_DIMENSION) {
            return bitmap
        }
        
        val scale = if (width > height) {
            MAX_IMAGE_DIMENSION.toFloat() / width
        } else {
            MAX_IMAGE_DIMENSION.toFloat() / height
        }
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Save a bitmap to a file.
     */
    suspend fun saveBitmapToFile(
        bitmap: Bitmap,
        file: File,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(format, quality, out)
                return@withContext true
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error saving bitmap to file", e)
            return@withContext false
        }
    }
    
    /**
     * Create a thumbnail from a bitmap.
     */
    fun createThumbnail(bitmap: Bitmap, maxSize: Int = 256): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }
        
        val scale = if (width > height) {
            maxSize.toFloat() / width
        } else {
            maxSize.toFloat() / height
        }
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
