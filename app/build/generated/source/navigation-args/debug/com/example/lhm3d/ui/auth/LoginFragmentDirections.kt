package com.example.lhm3d.ui.auth

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.lhm3d.R

public class LoginFragmentDirections private constructor() {
  public companion object {
    public fun actionLoginToHome(): NavDirections =
        ActionOnlyNavDirections(R.id.action_login_to_home)
  }
}
