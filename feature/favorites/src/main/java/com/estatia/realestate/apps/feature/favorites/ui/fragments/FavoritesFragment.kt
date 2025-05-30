package com.estatia.realestate.apps.feature.favorites.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.estatia.realestate.apps.core.common.misc.Consts
import com.estatia.realestate.apps.core.domain.interfaces.IExoplayer
import com.estatia.realestate.apps.core.model.property.Property
import com.estatia.realestate.apps.core.ui.adapters.PropertyAdapter
import com.estatia.realestate.apps.core.ui.navigation.DeepLinks
import com.estatia.realestate.apps.core.ui.viewmodels.PropertyViewModel
import com.application.real_estate_app.feature_favorites.R
import com.application.real_estate_app.feature_favorites.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.core.net.toUri
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository


@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var propertyAdapter: PropertyAdapter

    @Inject
    lateinit var authChecker: IAuthRepository // Inject Authentication API

    @Inject
    lateinit var exoPlayer: IExoplayer  // Inject ExoPlayer Media Player

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
            propertyViewModel.loadLikedProperties { exception ->
                Toast.makeText(requireContext(),
                    getString(R.string.error_loading_liked_properties, exception.message),
                    Toast.LENGTH_SHORT).show()
            } // Initial load of liked properties
        }
    }

    private fun setupRecyclerView() {
        val currentUserId = authChecker.getCurrentUserId()
        propertyAdapter = PropertyAdapter(
            viewModel = propertyViewModel,
            onClick = { propertyId -> propertyViewModel.fetchPropertyById(propertyId)
            { exception ->
                Toast.makeText(requireContext(),
                    getString(R.string.error_fetching_property, exception.message),
                    Toast.LENGTH_SHORT).show()
            } },
            onCommentClick = { propertyId -> navigateToComments(propertyId, currentUserId) },
            context = requireContext(),
            exoPlayer = exoPlayer
        )

        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = propertyAdapter
            propertyAdapter.attachRecyclerViewScrollListener(this)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            propertyViewModel.loadLikedProperties{ exception ->
                Toast.makeText(requireContext(),
                    getString(R.string.error_loading_liked_properties, exception.message),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        propertyViewModel.likedProperties.observe(viewLifecycleOwner) { properties ->
            updateUI(properties)
        }
    }

    private fun updateUI(properties: List<Property>?) {
        if (properties!!.isEmpty()) {
            binding.emptyStateTextView.visibility = View.VISIBLE
        } else {
            binding.emptyStateTextView.visibility = View.GONE
            propertyAdapter.submitList(properties)
        }
        binding.swipeRefreshLayout.isRefreshing = false
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


    override fun onPause() {
        super.onPause()
        exoPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        exoPlayer.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.detachPlayer()
        _binding = null
    }
}