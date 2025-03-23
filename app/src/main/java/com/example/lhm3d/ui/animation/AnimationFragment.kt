package com.example.lhm3d.ui.animation

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentAnimationBinding
import com.example.lhm3d.model.Animation
import com.example.lhm3d.model.AnimationSettings
import com.example.lhm3d.renderer.GLSurfaceRenderer
import com.example.lhm3d.viewmodel.AnimationViewModel
import com.google.android.material.chip.Chip

/**
 * AnimationFragment allows users to animate their 3D models with
 * various animations and control options.
 */
class AnimationFragment : Fragment() {

    private var _binding: FragmentAnimationBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AnimationViewModel by viewModels()
    private val args: AnimationFragmentArgs by navArgs()
    
    private var renderer: GLSurfaceRenderer? = null
    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupGLSurfaceView()
        setupAnimationChips()
        setupControls()
        observeViewModel()
        
        // Load model and animations
        viewModel.loadModel(args.modelId)
        viewModel.loadAnimations()
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
            
            // Render continuously for animations
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    /**
     * Set up animation chip group.
     */
    private fun setupAnimationChips() {
        binding.chipGroupAnimations.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val chip = group.findViewById<Chip>(checkedId)
                val animationName = chip.text.toString()
                viewModel.selectAnimation(animationName)
            }
        }
    }

    /**
     * Set up animation controls.
     */
    private fun setupControls() {
        // Play button
        binding.buttonPlay.setOnClickListener {
            isPlaying = true
            updatePlayPauseState()
            viewModel.playAnimation()
        }
        
        // Pause button
        binding.buttonPause.setOnClickListener {
            isPlaying = false
            updatePlayPauseState()
            viewModel.pauseAnimation()
        }
        
        // Reset button
        binding.buttonReset.setOnClickListener {
            viewModel.resetAnimation()
        }
        
        // Loop checkbox
        binding.checkBoxLoop.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setLooping(isChecked)
        }
        
        // Speed seekbar
        binding.seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speed = (progress / 50f) + 0.5f // Range from 0.5 to 2.5
                viewModel.setSpeed(speed)
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Save animation button
        binding.buttonSaveAnimation.setOnClickListener {
            val settings = AnimationSettings(
                speed = (binding.seekBarSpeed.progress / 50f) + 0.5f,
                loop = binding.checkBoxLoop.isChecked
            )
            viewModel.saveAnimation(settings)
        }
    }

    /**
     * Update the play/pause button state.
     */
    private fun updatePlayPauseState() {
        binding.buttonPlay.isEnabled = !isPlaying
        binding.buttonPause.isEnabled = isPlaying
    }

    /**
     * Observe the ViewModel's LiveData.
     */
    private fun observeViewModel() {
        // Observe model
        viewModel.model.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    val model = it.getOrNull()
                    if (model != null) {
                        // In a real app, you would load the 3D model into the renderer
                        renderer?.setModelPath(model.modelStoragePath)
                        binding.glSurfaceView.requestRender()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Model not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        it.exceptionOrNull()?.message ?: "Error loading model",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Observe animations
        viewModel.animations.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    val animations = it.getOrNull() ?: emptyList()
                    updateAnimationChips(animations)
                } else {
                    Toast.makeText(
                        requireContext(),
                        it.exceptionOrNull()?.message ?: "Error loading animations",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Observe save result
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(
                        requireContext(),
                        "Animation saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        it.exceptionOrNull()?.message ?: "Error saving animation",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.clearSaveResult()
            }
        }

        // Observe selected animation
        viewModel.selectedAnimation.observe(viewLifecycleOwner) { animation ->
            animation?.let {
                // In a real app, you would apply the animation to the renderer
                renderer?.setAnimation(it.id)
                binding.glSurfaceView.requestRender()
            }
        }
    }

    /**
     * Update the animation chips based on available animations.
     */
    private fun updateAnimationChips(animations: List<Animation>) {
        binding.chipGroupAnimations.removeAllViews()
        
        animations.forEach { animation ->
            val chip = layoutInflater.inflate(
                R.layout.item_animation_chip,
                binding.chipGroupAnimations,
                false
            ) as Chip
            
            chip.text = animation.name
            chip.id = View.generateViewId()
            
            // Mark premium animations
            if (animation.isPremium) {
                chip.setChipIconResource(android.R.drawable.btn_star)
            }
            
            binding.chipGroupAnimations.addView(chip)
        }
        
        // Select the first animation by default if available
        if (animations.isNotEmpty()) {
            (binding.chipGroupAnimations.getChildAt(0) as? Chip)?.isChecked = true
        }
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
