package com.example.lhm3d.ui.create

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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
                binding.generateButton.isEnabled = true
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
        
        viewModel = ViewModelProvider(this)[CreateViewModel::class.java]
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        binding.uploadButton.setOnClickListener {
            openGallery()
        }
        
        binding.cameraButton.setOnClickListener {
            // Open camera - would need to implement camera functionality
            // For simplicity, we're just using gallery for now
            openGallery()
        }
        
        binding.generateButton.setOnClickListener {
            selectedImageUri?.let { uri ->
                viewModel.generateModel(uri.toString())
                showProcessingState()
            }
        }
    }
    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }
    
    private fun showProcessingState() {
        binding.uploadContainer.visibility = View.GONE
        binding.processingContainer.visibility = View.VISIBLE
    }
    
    private fun observeViewModel() {
        viewModel.modelGenerationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ModelGenerationState.Processing -> {
                    binding.progressBar.progress = state.progress
                }
                is ModelGenerationState.Success -> {
                    // Navigate to model viewer with the new model ID
                    val action = CreateModelFragmentDirections.actionCreateToModelViewer(state.modelId)
                    findNavController().navigate(action)
                }
                is ModelGenerationState.Error -> {
                    // Show error message and reset UI
                    binding.uploadContainer.visibility = View.VISIBLE
                    binding.processingContainer.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}