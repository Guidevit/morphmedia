package com.example.lhm3d.ui.home

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.lhm3d.R
import kotlin.Int
import kotlin.String

public class HomeFragmentDirections private constructor() {
  private data class ActionHomeToAnimation(
    public val modelId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_home_to_animation

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("modelId", this.modelId)
        return result
      }
  }

  private data class ActionHomeToModelView(
    public val modelId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_home_to_modelView

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("modelId", this.modelId)
        return result
      }
  }

  public companion object {
    public fun actionHomeToCreate(): NavDirections =
        ActionOnlyNavDirections(R.id.action_home_to_create)

    public fun actionHomeToAnimation(modelId: String): NavDirections =
        ActionHomeToAnimation(modelId)

    public fun actionHomeToModelView(modelId: String): NavDirections =
        ActionHomeToModelView(modelId)

    public fun actionHomeToGallery(): NavDirections =
        ActionOnlyNavDirections(R.id.action_home_to_gallery)

    public fun actionHomeToTutorial(): NavDirections =
        ActionOnlyNavDirections(R.id.action_home_to_tutorial)
  }
}
