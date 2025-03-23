package com.example.lhm3d.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // Navigate to Create Model screen
        binding.cardCreate.setOnClickListener {
            findNavController().navigate(R.id.navigation_create)
        }
        
        // Navigate to Public Gallery screen
        binding.cardGallery.setOnClickListener {
            findNavController().navigate(R.id.navigation_gallery)
        }
        
        // Navigate to My Models screen
        binding.cardMyModels.setOnClickListener {
            findNavController().navigate(R.id.navigation_models)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}