package com.example.lhm3d.ui.preview

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.lhm3d.R
import kotlin.Int
import kotlin.String

public class PreviewFragmentDirections private constructor() {
  private data class ActionPreviewToAnimation(
    public val modelId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_preview_to_animation

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("modelId", this.modelId)
        return result
      }
  }

  public companion object {
    public fun actionPreviewToAnimation(modelId: String): NavDirections =
        ActionPreviewToAnimation(modelId)
  }
}
