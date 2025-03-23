package com.example.lhm3d.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.chip.Chip
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.lhm3d.LoginActivity
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentProfileBinding
import com.example.lhm3d.model.SubscriptionType
import com.example.lhm3d.model.User
import com.example.lhm3d.model.UserSettings
import com.example.lhm3d.viewmodel.UserViewModel

/**
 * ProfileFragment displays user information, subscription details,
 * and settings.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: UserViewModel by viewModels()
    
    // Store the fragment view reference
    private var fragmentView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Store the view reference
        fragmentView = view
        
        setupClickListeners(view)
        observeViewModel()
        
        // Load user data
        viewModel.loadUserData()
    }

    /**
     * Set up click listeners for buttons.
     */
    private fun setupClickListeners(view: View) {
        // Upgrade button
        val upgradeButton = view.findViewById<MaterialButton>(R.id.upgrade_button)
        upgradeButton?.setOnClickListener {
            showPremiumOptions()
        }
        
        // Logout button
        val logoutButton = view.findViewById<TextView>(R.id.logout_button)
        logoutButton?.setOnClickListener {
            viewModel.signOut()
            navigateToLogin()
        }
    }

    // We'll implement settings management in a future update

    /**
     * Observe the ViewModel's LiveData.
     */
    private fun observeViewModel() {
        // Observe user data
        viewModel.userData.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    val user = it.getOrNull()
                    if (user != null) {
                        updateUI(user)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        it.exceptionOrNull()?.message ?: "Error loading user data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Observe settings update result
        viewModel.settingsUpdateResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    // Settings updated successfully, no need to show a message
                } else {
                    Toast.makeText(
                        requireContext(),
                        it.exceptionOrNull()?.message ?: "Error updating settings",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.clearSettingsUpdateResult()
            }
        }
    }

    /**
     * Update the UI with user data.
     */
    private fun updateUI(user: User) {
        // Update profile info
        binding.profileName.text = user.displayName
        binding.profileEmail.text = user.email
        
        // Update subscription info
        val subscriptionChip = fragmentView?.findViewById<Chip>(R.id.subscription_chip)
        val subscriptionDetails = fragmentView?.findViewById<TextView>(R.id.subscription_details)
        val upgradeButton = fragmentView?.findViewById<MaterialButton>(R.id.upgrade_button)
        
        when (user.subscription) {
            SubscriptionType.FREE_TRIAL -> {
                subscriptionChip?.text = getString(R.string.free_trial)
                subscriptionDetails?.text = "Remaining credits: ${user.remainingCredits}/5"
                upgradeButton?.visibility = View.VISIBLE
            }
            SubscriptionType.PREMIUM_MONTHLY -> {
                subscriptionChip?.text = "Premium Monthly"
                subscriptionDetails?.text = "Unlimited credits"
                upgradeButton?.visibility = View.GONE
            }
            SubscriptionType.PREMIUM_YEARLY -> {
                subscriptionChip?.text = "Premium Yearly"
                subscriptionDetails?.text = "Unlimited credits"
                upgradeButton?.visibility = View.GONE
            }
        }
        
        // For now, we don't have switches in our layout, we'll implement them later
        // when we add the settings page
    }

    /**
     * Show premium subscription options.
     */
    private fun showPremiumOptions() {
        // In a real app, you would navigate to a subscription screen or show a dialog
        // with subscription options using the BillingService
        Toast.makeText(
            requireContext(),
            "Premium subscription options would be shown here",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Navigate to the login screen.
     */
    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fragmentView = null
    }
}
