package com.example.lhm3d

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.lhm3d.databinding.ActivityLoginBinding
import com.example.lhm3d.viewmodel.UserViewModel

/**
 * Activity for user login and signup.
 * This is the entry point of the application.
 */
class LoginActivity : AppCompatActivity() {
    
    // Launch the Google sign-in activity and handle the result
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is already logged in
        if (viewModel.isUserLoggedIn()) {
            navigateToMainActivity()
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
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            
            if (validateInputs(email, password)) {
                viewModel.signIn(email, password)
            }
        }

        // Sign up button
        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            
            if (validateInputs(email, password)) {
                // For signup, we'll use email as displayName for simplicity
                // In a real app, you'd have a separate field for the name
                val displayName = email.substringBefore('@')
                viewModel.signUp(email, password, displayName)
            }
        }

        // Google sign-in button
        binding.buttonGoogleSignIn.setOnClickListener {
            val signInIntent = viewModel.getGoogleSignInIntent()
            googleSignInLauncher.launch(signInIntent)
        }

        // Forgot password text
        binding.textViewForgotPassword.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(
                    this,
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
        viewModel.authState.observe(this, Observer { result ->
            result?.let {
                binding.progressBar.visibility = View.GONE
                
                if (it.isSuccess) {
                    navigateToMainActivity()
                } else {
                    Toast.makeText(
                        this,
                        it.exceptionOrNull()?.message ?: getString(R.string.error_login),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

        // Observe loading state
        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonLogin.isEnabled = !isLoading
            binding.buttonSignUp.isEnabled = !isLoading
            binding.buttonGoogleSignIn.isEnabled = !isLoading
        })

        // Observe password reset state
        viewModel.passwordResetState.observe(this, Observer { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(
                        this,
                        "Password reset email sent. Check your inbox.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        it.exceptionOrNull()?.message ?: "Failed to send reset email",
                        Toast.LENGTH_LONG
                    ).show()
                }
                viewModel.clearPasswordResetState()
            }
        })
    }

    /**
     * Validate login/signup inputs.
     */
    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.textInputLayoutEmail.error = "Email is required"
            return false
        } else {
            binding.textInputLayoutEmail.error = null
        }

        if (password.isEmpty()) {
            binding.textInputLayoutPassword.error = "Password is required"
            return false
        } else if (password.length < 6) {
            binding.textInputLayoutPassword.error = "Password must be at least 6 characters"
            return false
        } else {
            binding.textInputLayoutPassword.error = null
        }

        return true
    }

    /**
     * Navigate to MainActivity.
     */
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity to prevent going back
    }
}
