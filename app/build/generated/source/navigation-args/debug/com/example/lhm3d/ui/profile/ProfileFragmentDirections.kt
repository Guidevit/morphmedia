package com.example.lhm3d.ui.profile

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.lhm3d.R

public class ProfileFragmentDirections private constructor() {
  public companion object {
    public fun actionProfileToEditProfile(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profile_to_edit_profile)
  }
}
