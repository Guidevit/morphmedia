package com.example.lhm3d.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

/**
 * Utility functions for handling permissions.
 */
object PermissionUtils {
    
    // Permission constants
    val CAMERA_PERMISSION = Manifest.permission.CAMERA
    
    // Based on Android version, we need to use different permissions for storage
    val STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    /**
     * Check if the app has camera permission.
     */
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if the app has storage permission.
     */
    fun hasStoragePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            STORAGE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Show a dialog explaining why a permission is needed.
     */
    fun showPermissionRationaleDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String,
        positiveAction: (() -> Unit)?,
        negativeButtonText: String?,
        negativeAction: (() -> Unit)?
    ) {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ ->
                positiveAction?.invoke()
            }
        
        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText) { _, _ ->
                negativeAction?.invoke()
            }
        }
        
        builder.create().show()
    }
}
