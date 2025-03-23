package com.example.lhm3d.ui.create

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.lhm3d.R
import kotlin.Int
import kotlin.String

public class CreateFragmentDirections private constructor() {
  private data class ActionCreateToAnimation(
    public val modelId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_create_to_animation

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("modelId", this.modelId)
        return result
      }
  }

  public companion object {
    public fun actionCreateToAnimation(modelId: String): NavDirections =
        ActionCreateToAnimation(modelId)
  }
}
