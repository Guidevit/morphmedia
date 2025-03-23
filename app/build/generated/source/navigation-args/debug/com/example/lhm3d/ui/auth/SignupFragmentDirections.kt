package com.example.lhm3d.ui.auth

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.lhm3d.R

public class SignupFragmentDirections private constructor() {
  public companion object {
    public fun actionSignupToLogin(): NavDirections =
        ActionOnlyNavDirections(R.id.action_signup_to_login)

    public fun actionSignupToHome(): NavDirections =
        ActionOnlyNavDirections(R.id.action_signup_to_home)
  }
}
