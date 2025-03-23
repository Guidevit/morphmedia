package com.example.lhm3d.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lhm3d.R
import com.example.lhm3d.databinding.FragmentHomeBinding
import com.example.lhm3d.ui.model.ModelAdapter
import com.example.lhm3d.viewmodel.ViewModelFactory

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
        // Create the ViewModel with context
        val factory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

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
            val bundle = Bundle().apply {
                putString("modelId", modelId)
            }
            findNavController().navigate(R.id.action_home_to_modelView, bundle)
        }
        
        binding.recentModelsRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = modelAdapter
        }
        
        // Set up tutorial button
        binding.tutorialButton.setOnClickListener {
            // Navigate to tutorial
            findNavController().navigate(R.id.action_home_to_tutorial)
        }
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.recentModels.observe(viewLifecycleOwner) { models ->
            if (models.isEmpty()) {
                // No need to show empty state, just don't display models
                binding.recentModelsRecycler.visibility = View.GONE
            } else {
                binding.recentModelsRecycler.visibility = View.VISIBLE
                modelAdapter.submitList(models)
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                // Show error toast
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}