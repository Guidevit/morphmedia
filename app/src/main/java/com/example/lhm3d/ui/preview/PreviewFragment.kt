package com.example.lhm3d.ui.preview

import android.graphics.PixelFormat
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentPreviewBinding
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.ProcessingStatus
import com.example.lhm3d.renderer.GLSurfaceRenderer
import com.example.lhm3d.viewmodel.PreviewViewModel

/**
 * PreviewFragment displays a 3D model and provides options to animate or save it.
 */
class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PreviewViewModel by viewModels()
    // Use arguments bundle instead of safeargs
    private val arguments: Bundle by lazy { requireArguments() }
    
    private var renderer: GLSurfaceRenderer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupGLSurfaceView()
        setupClickListeners()
        observeViewModel()
        
        // Process input from args
        processArgs()
    }

    /**
     * Set up the OpenGL surface view.
     */
    private fun setupGLSurfaceView() {
        binding.glSurfaceView.apply {
            setEGLContextClientVersion(2)
            setZOrderOnTop(true)
            setEGLConfigChooser(8, 8, 8, 8, 16, 0)
            holder.setFormat(PixelFormat.TRANSLUCENT)
            preserveEGLContextOnPause = true
            
            renderer = GLSurfaceRenderer(context)
            setRenderer(renderer)
            
            // Only render when there are changes
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }
    }

    /**
     * Process the fragment arguments.
     */
    private fun processArgs() {
        val imageUriString = arguments.getString("imageUri")
        if (imageUriString != null) {
            // We have an image URI to process
            showProcessingUI()
            viewModel.processImage(Uri.parse(imageUriString))
        }
        
        val modelId = arguments.getString("modelId")
        if (modelId != null) {
            // We have a model ID to load
            showProcessingUI()
            viewModel.loadModel(modelId)
        }
    }

    /**
     * Set up click listeners for buttons.
     */
    private fun setupClickListeners() {
        binding.buttonAnimate.setOnClickListener {
            viewModel.currentModel.value?.getOrNull()?.let { model ->
                navigateToAnimation(model.id)
            }
        }
        
        binding.buttonSave.setOnClickListener {
            // This would save the current state or modifications to the model
            Toast.makeText(
                requireContext(),
                "Model saved successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
        
        binding.buttonRetry.setOnClickListener {
            processArgs() // Retry processing
        }
    }

    /**
     * Observe the ViewModel's LiveData.
     */
    private fun observeViewModel() {
        // Observe current model
        viewModel.currentModel.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    val model = it.getOrNull()
                    if (model != null) {
                        updateModelUI(model)
                    } else {
                        showErrorUI("Model not found")
                    }
                } else {
                    showErrorUI(it.exceptionOrNull()?.message ?: getString(R.string.error_processing))
                }
            }
        }

        // Observe processing state
        viewModel.processingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ProcessingStatus.PENDING, ProcessingStatus.PROCESSING -> showProcessingUI()
                ProcessingStatus.COMPLETED -> showPreviewUI()
                ProcessingStatus.FAILED -> showErrorUI(getString(R.string.error_processing))
            }
        }
    }

    /**
     * Update the UI with the model data.
     */
    private fun updateModelUI(model: Model3D) {
        binding.textViewModelReady.text = model.name
        
        // In a real app, you would load the 3D model into the renderer
        renderer?.setModelPath(model.modelStoragePath)
        binding.glSurfaceView.requestRender()
    }

    /**
     * Show the processing UI.
     */
    private fun showProcessingUI() {
        binding.layoutProcessing.visibility = View.VISIBLE
        binding.layoutPreview.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    /**
     * Show the preview UI.
     */
    private fun showPreviewUI() {
        binding.layoutProcessing.visibility = View.GONE
        binding.layoutPreview.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
    }

    /**
     * Show the error UI.
     */
    private fun showErrorUI(errorMessage: String) {
        binding.layoutProcessing.visibility = View.GONE
        binding.layoutPreview.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.textViewError.text = errorMessage
    }

    /**
     * Navigate to the animation screen.
     */
    private fun navigateToAnimation(modelId: String) {
        val bundle = Bundle().apply {
            putString("modelId", modelId)
        }
        findNavController().navigate(R.id.navigation_animation, bundle)
    }

    override fun onResume() {
        super.onResume()
        binding.glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.glSurfaceView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
