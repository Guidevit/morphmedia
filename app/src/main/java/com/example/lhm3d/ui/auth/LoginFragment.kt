package com.example.lhm3d.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentLoginBinding
import com.example.lhm3d.viewmodel.UserViewModel

/**
 * Fragment for user login and signup.
 */
class LoginFragment : Fragment() {
    
    // Launch the Google sign-in activity and handle the result
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user is already logged in
        if (viewModel.isUserLoggedIn()) {
            navigateToHome()
            return
        }

        setupClickListeners()
        observeViewModel()
    }

    /**
     * Set up click listeners for buttons.
     */
    private fun setupClickListeners() {
        // Login button
        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            
            if (validateInputs(email, password)) {
                viewModel.signIn(email, password)
            }
        }

        // Sign up button
        binding.signupButton.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            
            if (validateInputs(email, password)) {
                // For signup, we'll use email as displayName for simplicity
                // In a real app, you'd have a separate field for the name
                val displayName = email.substringBefore('@')
                viewModel.signUp(email, password, displayName)
            }
        }

        // Google sign-in button
        binding.googleSignIn.setOnClickListener {
            val signInIntent = viewModel.getGoogleSignInIntent()
            googleSignInLauncher.launch(signInIntent)
        }

        // Forgot password text
        binding.forgotPassword.setOnClickListener {
            val email = binding.email.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter your email address",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            
            viewModel.resetPassword(email)
        }
    }

    /**
     * Observe the ViewModel's LiveData.
     */
    private fun observeViewModel() {
        // Observe authentication state
        viewModel.authState.observe(viewLifecycleOwner) { result ->
            result?.let {
                binding.progressBar.visibility = View.GONE
                
                if (it.isSuccess) {
                    navigateToHome()
                } else {
                    Toast.makeText(
                        requireContext(),
                        it.exceptionOrNull()?.message ?: getString(R.string.error_login),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loginButton.isEnabled = !isLoading
            binding.signupButton.isEnabled = !isLoading
            binding.googleSignIn.isEnabled = !isLoading
        }

        // Observe password reset state
        viewModel.passwordResetState.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(
                        requireContext(),
                        "Password reset email sent. Check your inbox.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        it.exceptionOrNull()?.message ?: "Failed to send reset email",
                        Toast.LENGTH_LONG
                    ).show()
                }
                viewModel.clearPasswordResetState()
            }
        }
    }

    /**
     * Validate login/signup inputs.
     */
    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailLayout.error = "Email is required"
            return false
        } else {
            binding.emailLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            return false
        } else if (password.length < 6) {
            binding.passwordLayout.error = "Password must be at least 6 characters"
            return false
        } else {
            binding.passwordLayout.error = null
        }

        return true
    }

    /**
     * Navigate to Home fragment.
     */
    private fun navigateToHome() {
        findNavController().navigate(R.id.action_login_to_home)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}