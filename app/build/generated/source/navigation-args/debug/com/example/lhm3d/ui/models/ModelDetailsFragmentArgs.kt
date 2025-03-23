package com.example.lhm3d.ui.models

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class ModelDetailsFragmentArgs(
  public val modelId: String,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("modelId", this.modelId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("modelId", this.modelId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): ModelDetailsFragmentArgs {
      bundle.setClassLoader(ModelDetailsFragmentArgs::class.java.classLoader)
      val __modelId : String?
      if (bundle.containsKey("modelId")) {
        __modelId = bundle.getString("modelId")
        if (__modelId == null) {
          throw IllegalArgumentException("Argument \"modelId\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"modelId\" is missing and does not have an android:defaultValue")
      }
      return ModelDetailsFragmentArgs(__modelId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): ModelDetailsFragmentArgs {
      val __modelId : String?
      if (savedStateHandle.contains("modelId")) {
        __modelId = savedStateHandle["modelId"]
        if (__modelId == null) {
          throw IllegalArgumentException("Argument \"modelId\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"modelId\" is missing and does not have an android:defaultValue")
      }
      return ModelDetailsFragmentArgs(__modelId)
    }
  }
}
