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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Setup RecyclerViews for featured and recent models
        binding.buttonStartCreating.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_create)
        }
    }
    
    private fun observeViewModel() {
        // Observe LiveData from ViewModel
        viewModel.featuredModels.observe(viewLifecycleOwner) { models ->
            // Update featured models RecyclerView
        }
        
        viewModel.recentModels.observe(viewLifecycleOwner) { models ->
            // Update recent models RecyclerView
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}