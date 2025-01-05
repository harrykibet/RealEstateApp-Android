package com.application.real_estate_app.feature_favorites.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.real_estate_app.core.data_utils.media_players.ExoPlayerManager
import com.application.real_estate_app.core.data_utils.models.Property
import com.application.real_estate_app.core.interfaces.IAuthApiCore
import com.application.real_estate_app.feature_favorites.ui.adapters.FavoritesAdapter
import com.application.real_estate_app.feature_favorites.databinding.FragmentFavoritesBinding
import com.application.real_estate_app.feature_favorites.ui.viewModels.PropertyViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@UnstableApi
@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var propertyAdapter: FavoritesAdapter

    @Inject
    lateinit var authChecker: IAuthApiCore // Inject AuthService

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    private val propertyViewModel: PropertyViewModel by viewModels()

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
            propertyViewModel.loadLikedProperties() // Initial load of liked properties
        }
    }

    private fun setupRecyclerView() {
        val currentUserId = authChecker.getCurrentUserId()
        propertyAdapter = FavoritesAdapter(
            viewModel = propertyViewModel,
            onClick = { propertyId -> propertyViewModel.fetchPropertyById(propertyId) },
            onCommentClick = { propertyId -> navigateToComments(propertyId, currentUserId) },
            context = requireContext(),
            exoPlayer = exoPlayerManager
        )

        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = propertyAdapter
            propertyAdapter.attachRecyclerViewScrollListener(this)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            propertyViewModel.loadLikedProperties()
        }
    }

    private fun setupObservers() {
        propertyViewModel.likedProperties.observe(viewLifecycleOwner) { properties ->
            updateUI(properties)
        }
    }

    private fun updateUI(properties: List<Property>) {
        if (properties.isEmpty()) {
            binding.emptyStateTextView.visibility = View.VISIBLE
        } else {
            binding.emptyStateTextView.visibility = View.GONE
            propertyAdapter.submitList(properties)
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun navigateToComments(propertyId: String, currentUserId: String?) {
        val action = FavoritesFragmentDirections.actionFavoritesToComment(propertyId, currentUserId ?: "")
        findNavController().navigate(action)
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