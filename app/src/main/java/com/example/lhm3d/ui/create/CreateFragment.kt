package com.example.lhm3d.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lhm3d.databinding.FragmentCreateBinding

class CreateFragment : Fragment() {
    
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: CreateViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CreateViewModel::class.java]
        
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up image selection buttons
        binding.buttonUpload.setOnClickListener {
            // TODO: Implement image selection from gallery
        }
        
        binding.buttonTakePhoto.setOnClickListener {
            // TODO: Implement camera capture functionality
        }
        
        // Set up create button
        binding.buttonCreate.setOnClickListener {
            // TODO: Implement model creation with data validation
            val name = binding.editTextName.text.toString()
            val description = binding.editTextDescription.text.toString()
            val isPublic = binding.switchPublic.isChecked
            
            // Validate inputs
            if (name.isBlank()) {
                binding.editTextName.error = "Name is required"
                return@setOnClickListener
            }
            
            // TODO: Check if image is selected
            
            // Process model creation
            // viewModel.createModel(name, description, imageFile, isPublic)
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonCreate.isEnabled = !isLoading
        }
        
        // Observe creation result
        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            // TODO: Handle creation result (success/failure)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}