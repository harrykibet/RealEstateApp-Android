package com.application.real_estate_app.feature_favorites.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController import androidx.recyclerview.widget.LinearLayoutManager
import com.application.real_estate_app.core.common.misc.Consts
import com.application.real_estate_app.core.utils.media_players.ExoPlayerManager
import com.application.real_estate_app.core.domain.models.Property
import com.application.real_estate_app.core.domain.interfaces.AuthApiInterface
import com.application.real_estate_app.core.ui.navigation.DeepLinks
import com.application.real_estate_app.feature_favorites.R
import com.application.real_estate_app.feature_favorites.ui.adapters.FavoritesAdapter
import com.application.real_estate_app.feature_favorites.databinding.FragmentFavoritesBinding
import com.application.real_estate_app.feature_favorites.ui.viewmodels.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@UnstableApi
@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritesAdapter: FavoritesAdapter

    @Inject
    lateinit var authChecker: AuthApiInterface // Inject Authentication API

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager  // Inject ExoPlayer Media Player

    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupObservers()

        if (savedInstanceState == null) {
            favoritesViewModel.loadLikedProperties { exception ->
                Toast.makeText(requireContext(),
                    getString(R.string.error_loading_liked_properties, exception.message),
                    Toast.LENGTH_SHORT).show()
            } // Initial load of liked properties
        }
    }

    private fun setupRecyclerView() {
        val currentUserId = authChecker.getCurrentUserId()
        favoritesAdapter = FavoritesAdapter(
            viewModel = favoritesViewModel,
            onClick = { propertyId -> favoritesViewModel.fetchPropertyById(propertyId)
            { exception ->
                Toast.makeText(requireContext(),
                    getString(R.string.error_fetching_property, exception.message),
                    Toast.LENGTH_SHORT).show()
            } },
            onCommentClick = { propertyId -> navigateToComments(propertyId, currentUserId) },
            context = requireContext(),
            exoPlayer = exoPlayerManager
        )

        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritesAdapter
            favoritesAdapter.attachRecyclerViewScrollListener(this)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            favoritesViewModel.loadLikedProperties{ exception ->
                Toast.makeText(requireContext(),
                    getString(R.string.error_loading_liked_properties, exception.message),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        favoritesViewModel.likedProperties.observe(viewLifecycleOwner) { properties ->
            updateUI(properties)
        }
    }

    private fun updateUI(properties: List<Property>?) {
        if (properties!!.isEmpty()) {
            binding.emptyStateTextView.visibility = View.VISIBLE
        } else {
            binding.emptyStateTextView.visibility = View.GONE
            favoritesAdapter.submitList(properties)
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    // Navigate via deep link
    private fun navigateToComments(propertyId: String, currentUserId: String?) {
        // Construct the deep link URI using the constant
        val uri = Uri.parse(
            DeepLinks.COMMENT_FRAGMENT
            .replace(DeepLinks.PROPERTY_ID_PLACEHOLDER, propertyId)
            .replace(DeepLinks.USER_ID_PLACEHOLDER, currentUserId ?: Consts.EMPTY_STRING)
        )

        // Use NavController to navigate with the deep link
        findNavController().navigate(uri)
    }


    override fun onPause() {
        super.onPause()
        exoPlayerManager.pause()
    }

    override fun onResume() {
        super.onResume()
        exoPlayerManager.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayerManager.releasePlayer()
        _binding = null
    }
}