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
        binding.tabLayout.apply {
            addTab(this.newTab().setText(R.string.my_models))
            addTab(this.newTab().setText(R.string.community_models))
        }
        
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
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
    }
    
    private fun observeViewModel() {
        viewModel.models.observe(viewLifecycleOwner) { models ->
            // Update RecyclerView with models
            if (models.isEmpty()) {
                binding.emptyStateView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyStateView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                // Update adapter with models
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}