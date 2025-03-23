package com.example.lhm3d.ui.gallery

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.lhm3d.R
import kotlin.Int
import kotlin.String

public class GalleryFragmentDirections private constructor() {
  private data class ActionGalleryToModelDetails(
    public val modelId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_gallery_to_model_details

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("modelId", this.modelId)
        return result
      }
  }

  public companion object {
    public fun actionGalleryToModelDetails(modelId: String): NavDirections =
        ActionGalleryToModelDetails(modelId)
  }
}
