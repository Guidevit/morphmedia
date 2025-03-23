package com.example.lhm3d.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        
        setupClickListeners()
        setupSwitchListeners()
        observeViewModel()
        
        // Load user data
        viewModel.loadUserData()
    }

    /**
     * Set up click listeners for buttons.
     */
    private fun setupClickListeners() {
        // Edit profile button
        binding.buttonEditProfile.setOnClickListener {
            // In a real app, you would show an edit profile dialog or navigate to an edit profile screen
            Toast.makeText(
                requireContext(),
                "Edit profile functionality would be implemented here",
                Toast.LENGTH_SHORT
            ).show()
        }
        
        // Upgrade button
        binding.buttonUpgrade.setOnClickListener {
            showPremiumOptions()
        }
        
        // Logout button
        binding.buttonLogout.setOnClickListener {
            viewModel.signOut()
            navigateToLogin()
        }
    }

    /**
     * Set up listeners for setting switches.
     */
    private fun setupSwitchListeners() {
        // We'll capture all setting changes and update them together when any changes
        val settingChangedListener = { _: Boolean -> updateSettingsFromUI() }
        
        binding.switchNotifications.setOnCheckedChangeListener { _, _ -> updateSettingsFromUI() }
        binding.switchAutoSave.setOnCheckedChangeListener { _, _ -> updateSettingsFromUI() }
        binding.switchHighQuality.setOnCheckedChangeListener { _, _ -> updateSettingsFromUI() }
    }

    /**
     * Update user settings from UI controls.
     */
    private fun updateSettingsFromUI() {
        val settings = UserSettings(
            notifications = binding.switchNotifications.isChecked,
            autoSave = binding.switchAutoSave.isChecked,
            highQualityRendering = binding.switchHighQuality.isChecked
        )
        viewModel.updateUserSettings(settings)
    }

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
        binding.textViewUsername.text = user.displayName
        binding.textViewEmail.text = user.email
        
        // Update subscription info
        when (user.subscription) {
            SubscriptionType.FREE_TRIAL -> {
                binding.textViewCurrentPlan.text = getString(R.string.free_trial)
                binding.textViewRemainingCredits.text = "Remaining credits: ${user.remainingCredits}/5"
                binding.buttonUpgrade.visibility = View.VISIBLE
            }
            SubscriptionType.PREMIUM_MONTHLY -> {
                binding.textViewCurrentPlan.text = "Premium Monthly"
                binding.textViewRemainingCredits.text = "Unlimited credits"
                binding.buttonUpgrade.visibility = View.GONE
            }
            SubscriptionType.PREMIUM_YEARLY -> {
                binding.textViewCurrentPlan.text = "Premium Yearly"
                binding.textViewRemainingCredits.text = "Unlimited credits"
                binding.buttonUpgrade.visibility = View.GONE
            }
        }
        
        // Update settings
        binding.switchNotifications.isChecked = user.settings.notifications
        binding.switchAutoSave.isChecked = user.settings.autoSave
        binding.switchHighQuality.isChecked = user.settings.highQualityRendering
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
    }
}
