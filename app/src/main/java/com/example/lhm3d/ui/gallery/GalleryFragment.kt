package com.example.lhm3d.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[GalleryViewModel::class.java]
        
        setupTabs()
        setupRecyclerView()
        observeViewModel()
    }
    
    private fun setupTabs() {
        val tabLayout = binding.tabLayout
        
        // Add "My Models" tab
        tabLayout.addTab(tabLayout.newTab().setText("My Models"))
        
        // Add "Community Models" tab
        tabLayout.addTab(tabLayout.newTab().setText("Community Models"))
        
        tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.showMyModels()
                    1 -> viewModel.showCommunityModels()
                }
            }
            
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }
    
    private fun setupRecyclerView() {
        // Setup RecyclerView adapter for models
        // Will be implemented in future with proper ModelAdapter
    }
    
    private fun observeViewModel() {
        viewModel.models.observe(viewLifecycleOwner) { models ->
            // Update RecyclerView with models
            if (models.isEmpty()) {
                binding.emptyText.visibility = View.VISIBLE
                binding.modelsRecycler.visibility = View.GONE
            } else {
                binding.emptyText.visibility = View.GONE
                binding.modelsRecycler.visibility = View.VISIBLE
                // Update adapter with models
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}