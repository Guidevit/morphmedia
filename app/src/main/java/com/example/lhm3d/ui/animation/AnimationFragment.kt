package com.example.lhm3d.ui.animation

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import kotlin.properties.Delegates
import kotlin.reflect.KProperty
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
    private lateinit var chipGroupAnimations: com.google.android.material.chip.ChipGroup
    private lateinit var glSurfaceView: GLSurfaceView
    
    // UI Controls
    private var playButton: com.google.android.material.button.MaterialButton? = null
    private var pauseButton: com.google.android.material.button.MaterialButton? = null
    private var resetButton: com.google.android.material.button.MaterialButton? = null
    private var saveButton: com.google.android.material.button.MaterialButton? = null
    private var loopCheckbox: android.widget.CheckBox? = null
    private var speedSeekbar: android.widget.SeekBar? = null

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
        // Create GLSurfaceView programmatically and add it to the model_container
        val glSurfaceView = GLSurfaceView(requireContext())
        glSurfaceView.apply {
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
        
        // Find the model_container and replace model_view with our GLSurfaceView
        val container = binding.root.findViewById<ViewGroup>(R.id.model_container)
        val modelView = binding.root.findViewById<View>(R.id.model_view)
        if (container != null && modelView != null) {
            val index = container.indexOfChild(modelView)
            container.removeView(modelView)
            container.addView(glSurfaceView, index)
        } else {
            // Fallback if the views weren't found
            container?.addView(glSurfaceView)
        }
        
        // Store the glSurfaceView reference for later use
        this.glSurfaceView = glSurfaceView
    }

    /**
     * Set up animation chip group.
     */
    private fun setupAnimationChips() {
        // Create the chip group if needed
        if (!::chipGroupAnimations.isInitialized) {
            chipGroupAnimations = com.google.android.material.chip.ChipGroup(requireContext())
            chipGroupAnimations.id = View.generateViewId()
            binding.root.findViewById<ViewGroup>(R.id.control_panel)?.addView(chipGroupAnimations)
        }
        
        chipGroupAnimations.setOnCheckedChangeListener { group, checkedId ->
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
        // Find control elements from layout
        val playButton = binding.root.findViewById<com.google.android.material.button.MaterialButton>(R.id.play_button)
        val pauseButton = binding.root.findViewById<com.google.android.material.button.MaterialButton>(R.id.pause_button)
        val resetButton = binding.root.findViewById<com.google.android.material.button.MaterialButton>(R.id.reset_button)
        val loopCheckbox = binding.root.findViewById<android.widget.CheckBox>(R.id.loop_checkbox)
        val speedSeekbar = binding.root.findViewById<android.widget.SeekBar>(R.id.speed_slider)
        val saveButton = binding.root.findViewById<com.google.android.material.button.MaterialButton>(R.id.save_button)
        
        // Store references to controls
        this.playButton = playButton
        this.pauseButton = pauseButton
        this.resetButton = resetButton
        this.loopCheckbox = loopCheckbox
        this.speedSeekbar = speedSeekbar
        this.saveButton = saveButton
        
        // Play button
        playButton?.setOnClickListener {
            isPlaying = true
            updatePlayPauseState(playButton, pauseButton)
            viewModel.playAnimation()
        }
        
        // Pause button
        pauseButton?.setOnClickListener {
            isPlaying = false
            updatePlayPauseState(playButton, pauseButton)
            viewModel.pauseAnimation()
        }
        
        // Reset button
        resetButton?.setOnClickListener {
            viewModel.resetAnimation()
        }
        
        // Loop checkbox
        loopCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setLooping(isChecked)
        }
        
        // Speed seekbar
        speedSeekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speed = (progress / 50f) + 0.5f // Range from 0.5 to 2.5
                viewModel.setSpeed(speed)
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Save animation button
        saveButton?.setOnClickListener {
            val settings = AnimationSettings(
                speed = (speedSeekbar?.progress ?: 50) / 50f + 0.5f,
                loop = loopCheckbox?.isChecked ?: true
            )
            viewModel.saveAnimation(settings)
        }
        
        // All references already stored above
    }

    /**
     * Update the play/pause button state.
     */
    private fun updatePlayPauseState(playButton: com.google.android.material.button.MaterialButton?, pauseButton: com.google.android.material.button.MaterialButton?) {
        playButton?.isEnabled = !isPlaying
        pauseButton?.isEnabled = isPlaying
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
                        if (::glSurfaceView.isInitialized) {
                            glSurfaceView.requestRender()
                        }
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
                if (::glSurfaceView.isInitialized) {
                    glSurfaceView.requestRender()
                }
            }
        }
    }

    /**
     * Update the animation chips based on available animations.
     */
    private fun updateAnimationChips(animations: List<Animation>) {
        // Create a chip group programmatically if not present in the layout
        if (!::chipGroupAnimations.isInitialized) {
            val chipGroup = com.google.android.material.chip.ChipGroup(requireContext())
            chipGroup.id = View.generateViewId()
            binding.root.findViewById<ViewGroup>(R.id.control_panel)?.addView(chipGroup)
            chipGroupAnimations = chipGroup
        }
        
        chipGroupAnimations.removeAllViews()
        
        animations.forEach { animation ->
            val chip = layoutInflater.inflate(
                R.layout.item_animation_chip,
                chipGroupAnimations,
                false
            ) as Chip
            
            chip.text = animation.name
            chip.id = View.generateViewId()
            
            // Mark premium animations
            if (animation.isPremium) {
                chip.setChipIconResource(android.R.drawable.btn_star)
            }
            
            chipGroupAnimations.addView(chip)
        }
        
        // Select the first animation by default if available
        if (animations.isNotEmpty()) {
            (chipGroupAnimations.getChildAt(0) as? Chip)?.isChecked = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (::glSurfaceView.isInitialized) {
            glSurfaceView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::glSurfaceView.isInitialized) {
            glSurfaceView.onPause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
