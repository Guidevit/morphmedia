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
import com.example.lhm3d.ui.adapters.ModelAdapter
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var featuredAdapter: ModelAdapter
    private lateinit var recentAdapter: ModelAdapter
    
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
        
        setupViewModel()
        setupAdapters()
        observeViewModel()
        setupListeners()
        
        // Check if user is logged in, if not redirect to login
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // In a real app, navigate to login
            // For now, just show login message
            binding.loginMessage.visibility = View.VISIBLE
        } else {
            binding.loginMessage.visibility = View.GONE
        }
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }
    
    private fun setupAdapters() {
        // Featured models recycler view
        featuredAdapter = ModelAdapter { model ->
            // Navigate to model detail
            val action = HomeFragmentDirections.actionHomeToModelDetail(model.id)
            findNavController().navigate(action)
        }
        binding.featuredRecyclerView.apply {
            adapter = featuredAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), 
                LinearLayoutManager.HORIZONTAL, 
                false
            )
        }
        
        // Recent models recycler view
        recentAdapter = ModelAdapter { model ->
            // Navigate to model detail
            val action = HomeFragmentDirections.actionHomeToModelDetail(model.id)
            findNavController().navigate(action)
        }
        binding.recentRecyclerView.apply {
            adapter = recentAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), 
                LinearLayoutManager.HORIZONTAL, 
                false
            )
        }
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.featuredModels.observe(viewLifecycleOwner) { models ->
            featuredAdapter.submitList(models)
            binding.emptyFeaturedView.visibility = if (models.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.recentModels.observe(viewLifecycleOwner) { models ->
            recentAdapter.submitList(models)
            binding.emptyRecentView.visibility = if (models.isEmpty()) View.VISIBLE else View.GONE
        }
    }
    
    private fun setupListeners() {
        binding.createFab.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_create)
        }
        
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
            binding.swipeRefresh.isRefreshing = false
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}