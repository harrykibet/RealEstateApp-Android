package com.application.real_estate_app.feature_home.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.real_estate_app.domain.interfaces.AuthRepository
import com.application.real_estate_app.feature_home.adapters.PropertyAdapter
import com.application.real_estate_app.feature_home.databinding.FragmentHomeBinding
import com.application.real_estate_app.feature_home.exoplayer.ExoPlayerManager
import com.application.real_estate_app.feature_home.viewModels.HomeViewModel
import com.application.real_estate_app.feature_home.viewModels.LikeStatus
import com.application.real_estate_app.feature_home.viewModels.PropertyViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var propertyAdapter: PropertyAdapter

    @Inject
    lateinit var authChecker: AuthRepository
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    private val homeViewModel: HomeViewModel by viewModels()
    private val propertyViewModel: PropertyViewModel by viewModels()


    private val pageSize = 10 // Pagination size

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController() // Initialize NavController

        // Assuming you're using FirebaseAuth for authentication
        val currentUserId = authChecker.getCurrentUserId()

        // Initialize  PropertyAdapter
        propertyAdapter = PropertyAdapter(
            viewModel = propertyViewModel, // Your ViewModel instance
            onClick = { propertyId ->
                // Handle property item click: fetch detailed property data
                propertyViewModel.fetchPropertyById(propertyId)
            },
            onCommentClick = { propertyId ->
                // Handle comment button click: navigate to CommentFragment
                val action =
                    HomeFragmentDirections.actionHomeToComment(propertyId, currentUserId ?: "")
                navController.navigate(action)
            },
            exoPlayer = exoPlayerManager,
            context = requireContext()
        )
        // Set up RecyclerView and adapter
        binding.featuredPropertiesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = propertyAdapter
            // Attach the custom scroll listener to handle video playback
            propertyAdapter.attachRecyclerViewScrollListener(this)
        }


        // Initialize SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Avoid repeated refreshes
            if (!binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = true
                homeViewModel.fetchProperties(isFirstLoad = true, pageSize = pageSize)
            }
        }

        // Observe properties and manage placeholder visibility
        homeViewModel.propertiesLiveData.observe(viewLifecycleOwner) { properties ->
            // Stop the refresh animation when data is loaded
            binding.swipeRefreshLayout.isRefreshing = false
            if (properties.isNullOrEmpty()) {
                // Show placeholder and hide RecyclerView
                binding.noPropertiesPlaceholder.visibility = View.VISIBLE
                binding.featuredPropertiesList.visibility = View.GONE
            } else {
                // Hide placeholder and show RecyclerView
                binding.noPropertiesPlaceholder.visibility = View.GONE
                binding.featuredPropertiesList.visibility = View.VISIBLE
                propertyAdapter.submitList(properties)
            }
        }

        // Observe error messages
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.swipeRefreshLayout.isRefreshing = false
            // Show error message to the user (e.g., Toast or SnackBar)
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Load properties (initial load)
        if (savedInstanceState == null) {
            homeViewModel.fetchProperties(isFirstLoad = true, pageSize = pageSize)
        }

        // Set up scroll listener for pagination
        setupScrollListener()

        // Observe individual property details from PropertyViewModel (optional, if needed)
        propertyViewModel.propertyLiveData.observe(viewLifecycleOwner) {
            // Handle the property details (e.g., navigate to a detailed view)
        }


        // Observe liked status from PropertyViewModel
        propertyViewModel.likedStatus.observe(viewLifecycleOwner) { likedStatus ->
            when (likedStatus) {
                LikeStatus.LIKE_SUCCESS -> Log.d("PropertyLike", "Property liked successfully!")
                LikeStatus.UNLIKE_SUCCESS -> Log.d("PropertyLike", "Property unliked successfully!")
                LikeStatus.LIKE_ERROR -> Log.e("PropertyLike", "Error liking property.")
                LikeStatus.UNLIKE_ERROR -> Log.e("PropertyLike", "Error unliking property.")
                else -> Log.w("PropertyLike", "Unknown like status.")
            }
        }
    }


    // Scroll listener to trigger pagination
    private fun setupScrollListener() {
        binding.featuredPropertiesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (shouldLoadMore()) {
                    homeViewModel.fetchProperties(isFirstLoad = false, pageSize = pageSize)
                }
            }
        })
    }

    private fun shouldLoadMore(): Boolean {
        val layoutManager = binding.featuredPropertiesList.layoutManager as LinearLayoutManager
        return layoutManager.findLastVisibleItemPosition() >= propertyAdapter.itemCount - 3 && homeViewModel.canLoadMore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayerManager.releasePlayer()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        exoPlayerManager.pause()
    }

    override fun onResume() {
        super.onResume()
        exoPlayerManager.resume()
    }
}