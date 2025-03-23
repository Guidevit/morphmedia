package com.example.lhm3d.ui.preview

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class PreviewFragmentArgs(
  public val imageUri: String?,
  public val modelId: String?,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("imageUri", this.imageUri)
    result.putString("modelId", this.modelId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("imageUri", this.imageUri)
    result.set("modelId", this.modelId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): PreviewFragmentArgs {
      bundle.setClassLoader(PreviewFragmentArgs::class.java.classLoader)
      val __imageUri : String?
      if (bundle.containsKey("imageUri")) {
        __imageUri = bundle.getString("imageUri")
      } else {
        throw IllegalArgumentException("Required argument \"imageUri\" is missing and does not have an android:defaultValue")
      }
      val __modelId : String?
      if (bundle.containsKey("modelId")) {
        __modelId = bundle.getString("modelId")
      } else {
        throw IllegalArgumentException("Required argument \"modelId\" is missing and does not have an android:defaultValue")
      }
      return PreviewFragmentArgs(__imageUri, __modelId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): PreviewFragmentArgs {
      val __imageUri : String?
      if (savedStateHandle.contains("imageUri")) {
        __imageUri = savedStateHandle["imageUri"]
      } else {
        throw IllegalArgumentException("Required argument \"imageUri\" is missing and does not have an android:defaultValue")
      }
      val __modelId : String?
      if (savedStateHandle.contains("modelId")) {
        __modelId = savedStateHandle["modelId"]
      } else {
        throw IllegalArgumentException("Required argument \"modelId\" is missing and does not have an android:defaultValue")
      }
      return PreviewFragmentArgs(__imageUri, __modelId)
    }
  }
}
