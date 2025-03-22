package com.application.real_estate_app.feature_home.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.real_estate_app.core_common.misc.Consts
import com.application.real_estate_app.core_interface.IAuthRepository
import com.application.real_estate_app.core_interface.IExoplayer
import com.application.real_estate_app.core_interface.LoggerInterface
import com.application.real_estate_app.core_ui.adapters.PropertyAdapter
import com.application.real_estate_app.core_ui.navigation.DeepLinks
import com.application.real_estate_app.core_ui.viewmodels.LikeStatus
import com.application.real_estate_app.feature_home.R
import com.application.real_estate_app.feature_home.databinding.FragmentHomeBinding
import com.application.real_estate_app.feature_home.ui.viewModels.HomeViewModel
import com.application.real_estate_app.core_ui.viewmodels.PropertyViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.core.net.toUri

@UnstableApi
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var propertyAdapter: PropertyAdapter

    @Inject
    lateinit var authApi: IAuthRepository
    @Inject
    lateinit var exoPlayer: IExoplayer
    @Inject
    lateinit var logger: LoggerInterface

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

        val currentUserId = authApi.getCurrentUserId()

        // Initialize  PropertyAdapter
        propertyAdapter = PropertyAdapter(
            viewModel = propertyViewModel,
            onClick = { propertyId -> propertyViewModel.fetchPropertyById(propertyId)  // fetch detailed property data
            { exception ->
                Toast.makeText(requireContext(), getString(R.string.error_fetching_properties, exception.message), Toast.LENGTH_SHORT).show()
            } },
            onCommentClick = { propertyId -> navigateToComments(propertyId, currentUserId) },
            exoPlayer = exoPlayer,
            context = requireContext())


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
                homeViewModel.fetchProperties(isFirstLoad = true, pageSize = pageSize) { exception ->
                    Toast.makeText(requireContext(), getString(R.string.error_fetching_properties, exception.message), Toast.LENGTH_SHORT).show()
                }
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
            homeViewModel.fetchProperties(isFirstLoad = true, pageSize = pageSize) { exception ->
                Toast.makeText(requireContext(), getString(R.string.error_fetching_properties, exception.message), Toast.LENGTH_SHORT).show()
            }
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
                LikeStatus.LIKE_SUCCESS -> logger.d("PropertyLike: Property liked successfully!")
                LikeStatus.UNLIKE_SUCCESS -> logger.d("PropertyLike: Property unliked successfully!")
                LikeStatus.LIKE_ERROR -> logger.d("PropertyLike: Error liking property.")
                LikeStatus.UNLIKE_ERROR -> logger.d("PropertyLike: Error unliking property.")
                else -> logger.w("PropertyLike: Unknown like status.")
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
                    { exception ->
                        Toast.makeText(requireContext(), getString(R.string.error_fetching_properties, exception.message), Toast.LENGTH_SHORT).show()}
                }
            }
        })
    }


    // Navigate via deep link
    private fun navigateToComments(propertyId: String, currentUserId: String?) {
        // Construct the deep link URI using the constant
        val uri = DeepLinks.COMMENT_FRAGMENT
            .replace(DeepLinks.PROPERTY_ID_PLACEHOLDER, propertyId)
            .replace(DeepLinks.USER_ID_PLACEHOLDER, currentUserId ?: Consts.EMPTY_STRING).toUri()

        // Use NavController to navigate with the deep link
        findNavController().navigate(uri)
    }


    private fun shouldLoadMore(): Boolean {
        val layoutManager = binding.featuredPropertiesList.layoutManager as LinearLayoutManager
        return layoutManager.findLastVisibleItemPosition() >= propertyAdapter.itemCount - 3 && homeViewModel.canLoadMore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.detachPlayer()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        exoPlayer.resume()
    }
}