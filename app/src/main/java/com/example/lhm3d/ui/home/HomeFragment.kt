package com.example.lhm3d.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentHomeBinding
import com.example.lhm3d.ui.model.ModelAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var modelAdapter: ModelAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()
        
        // Load recent models
        viewModel.loadRecentModels()

        return root
    }
    
    private fun setupUI() {
        // Set up RecyclerView
        modelAdapter = ModelAdapter { modelId ->
            // Navigate to model view
            val action = HomeFragmentDirections.actionHomeToModelView(modelId)
            findNavController().navigate(action)
        }
        
        binding.recyclerViewRecentModels.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = modelAdapter
        }
        
        // Set up buttons
        binding.buttonCreateNew.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_create)
        }
        
        binding.buttonViewGallery.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_gallery)
        }
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.recentModels.observe(viewLifecycleOwner) { models ->
            if (models.isEmpty()) {
                binding.textNoModels.visibility = View.VISIBLE
                binding.recyclerViewRecentModels.visibility = View.GONE
            } else {
                binding.textNoModels.visibility = View.GONE
                binding.recyclerViewRecentModels.visibility = View.VISIBLE
                modelAdapter.submitList(models)
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.textNoModels.text = getString(R.string.error_generic)
                binding.textNoModels.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}