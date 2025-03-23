package com.example.lhm3d.ui.home

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.lhm3d.R
import kotlin.Int
import kotlin.String

public class HomeFragmentDirections private constructor() {
  private data class ActionHomeToModelDetails(
    public val modelId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_home_to_model_details

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("modelId", this.modelId)
        return result
      }
  }

  public companion object {
    public fun actionHomeToModelDetails(modelId: String): NavDirections =
        ActionHomeToModelDetails(modelId)
  }
}
