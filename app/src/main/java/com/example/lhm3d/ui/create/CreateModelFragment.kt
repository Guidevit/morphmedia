package com.example.lhm3d.ui.create

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentCreateModelBinding

class CreateModelFragment : Fragment() {

    private var _binding: FragmentCreateModelBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: CreateViewModel
    
    private var selectedImageUri: Uri? = null
    
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.imagePreview.setImageURI(uri)
                binding.imagePreview.visibility = View.VISIBLE
                binding.textNoImage.visibility = View.GONE
                binding.buttonGenerate.isEnabled = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateModelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Create a ViewModel factory to pass the context
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateViewModel(requireContext()) as T
            }
        }
        
        viewModel = ViewModelProvider(this, factory)[CreateViewModel::class.java]
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        binding.buttonUploadImage.setOnClickListener {
            openGallery()
        }
        
        binding.buttonTakePhoto.setOnClickListener {
            // Open camera - would need to implement camera functionality
            // For simplicity, we're just using gallery for now
            openGallery()
        }
        
        binding.buttonGenerate.setOnClickListener {
            selectedImageUri?.let { uri ->
                // Use the Create method from CreateViewModel
                val name = "New 3D Model" // In a real app, this would be from user input
                val description = "Created from image" // In a real app, this would be from user input
                val isPublic = true // In a real app, this would be from user input
                
                viewModel.createModel(name, description, uri, isPublic)
                showProcessingState()
            }
        }
        
        // Set up detail level slider listener
        binding.sliderDetailLevel.addOnChangeListener { _, value, _ ->
            binding.textDetailLevel.text = "Detail Level: ${value.toInt()}"
        }
    }
    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }
    
    private fun showProcessingState() {
        // Show progress indicator
        binding.progressIndicator.visibility = View.VISIBLE
        binding.textProcessing.visibility = View.VISIBLE
        binding.buttonGenerate.isEnabled = false
    }
    
    private fun observeViewModel() {
        // Handle loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.textProcessing.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonGenerate.isEnabled = !isLoading
        }
        
        // Handle creation result
        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { modelId ->
                    // Navigate to model details with the new model ID
                    Toast.makeText(requireContext(), "Model created successfully!", Toast.LENGTH_SHORT).show()
                    try {
                        val action = CreateModelFragmentDirections.actionCreateToModelDetails(modelId)
                        findNavController().navigate(action)
                    } catch (e: Exception) {
                        // Fallback navigation if action doesn't exist
                        Toast.makeText(requireContext(), "Model created with ID: $modelId", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { error ->
                    // Show error message and reset UI
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    binding.progressIndicator.visibility = View.GONE
                    binding.textProcessing.visibility = View.GONE
                    binding.buttonGenerate.isEnabled = true
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}