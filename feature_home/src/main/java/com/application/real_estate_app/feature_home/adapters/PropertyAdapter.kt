package com.application.real_estate_app.feature_home.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.application.real_estate_app.feature_home.R
import com.application.real_estate_app.feature_home.databinding.PropertyItemBinding
import com.application.real_estate_app.domain.models.Property
import com.application.real_estate_app.feature_home.viewModels.PropertyViewModel
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.real_estate_app.feature_home.exoplayer.ExoPlayerManager

// Refactor the adapter for performance and reduce the functions it handle

@OptIn(UnstableApi::class) //ExoplayerManager is marked as unstable
class PropertyAdapter (
    private val viewModel: PropertyViewModel,
    private val onClick: (String) -> Unit,
    private val onCommentClick: (String) -> Unit,
    private val exoPlayer: ExoPlayerManager,
    private val context: Context
) : ListAdapter<Property, PropertyAdapter.PropertyViewHolder>(PropertyDiffCallback()) {

    private var currentlyPlayingPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding =
            PropertyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding, viewModel, onClick, exoPlayer, onCommentClick, context)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = getItem(position)
        holder.bind(property)

        if (property.video && property.videoUrl.isNotEmpty()) {
            if (position == currentlyPlayingPosition) {
                holder.reattachExoPlayer(property.videoUrl.first())
            } else {
                holder.releaseExoPlayer()
            }
            preloadMedia(position)
        }
    }

    override fun onViewRecycled(holder: PropertyViewHolder) {
        super.onViewRecycled(holder)
        holder.releaseExoPlayer()
    }


    fun attachRecyclerViewScrollListener(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handleVideoPlayback(recyclerView)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeVisibleVideo(recyclerView)
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    pauseCurrentVideo(recyclerView)
                }
            }
        })
    }

    private fun handleVideoPlayback(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        if (currentlyPlayingPosition !in firstVisiblePosition..lastVisiblePosition) {
            pauseCurrentVideo(recyclerView)
            currentlyPlayingPosition = RecyclerView.NO_POSITION
        }

        val visiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        if (visiblePosition != RecyclerView.NO_POSITION &&
            visiblePosition != currentlyPlayingPosition
        ) {
            val property = currentList.getOrNull(visiblePosition)
            if (property?.video == true) {
                playVideoAtPosition(visiblePosition, recyclerView)
            }
        }
    }

    private fun playVideoAtPosition(position: Int, recyclerView: RecyclerView) {
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as? PropertyViewHolder
        holder?.playExoPlayer()
        currentlyPlayingPosition = position
    }

    private fun pauseCurrentVideo(recyclerView: RecyclerView) {
        if (currentlyPlayingPosition != RecyclerView.NO_POSITION) {
            val holder = recyclerView.findViewHolderForAdapterPosition(currentlyPlayingPosition) as? PropertyViewHolder
            holder?.releaseExoPlayer()
            currentlyPlayingPosition = RecyclerView.NO_POSITION
        }
    }

    private fun resumeVisibleVideo(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val visiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()

        if (visiblePosition != RecyclerView.NO_POSITION &&
            visiblePosition != currentlyPlayingPosition
        ) {
            val property = currentList.getOrNull(visiblePosition)
            if (property?.video == true) {
                playVideoAtPosition(visiblePosition, recyclerView)
            }
        }
    }

    // Preload video for next property
    private fun preloadMedia(position: Int) {
        val nextPosition = position + 1
        if (nextPosition < currentList.size) {
            val nextProperty = getItem(nextPosition)
            if (nextProperty.video && nextProperty.videoUrl.isNotEmpty()) {
                exoPlayer.preloadMedia(nextProperty.videoUrl.first())
            }
        }
    }

    class PropertyViewHolder
        (
        private val binding: PropertyItemBinding,
        private val viewModel: PropertyViewModel,
        private val onClick: (String) -> Unit,
        private val exoPlayer: ExoPlayerManager,
        private val onCommentClick: (String) -> Unit,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        private val imagePagerAdapter = ImagePagerAdapter()
        private var property: Property? = null
        private var isLiked: Boolean = false
        private var isVideoPlaying = false // Track the video state

        init {
            binding.propertyImagePager.adapter = imagePagerAdapter
            setupListeners()
            setupViewPagerIndicator()
        }

        fun bind(property: Property) {
            this.property = property
            binding.propertyTitle.text = property.title
            binding.contentDescription.text = property.description

            // Handle video or image loading
            if (property.video && property.videoUrl.isNotEmpty()) {
                binding.propertyVideoPlayer.visibility = View.VISIBLE
                binding.propertyImagePager.visibility = View.GONE
                exoPlayer.attachPlayerToView(binding.propertyVideoPlayer, property.videoUrl.first())
                isVideoPlaying = true
            } else {
                binding.propertyVideoPlayer.visibility = View.GONE
                binding.propertyImagePager.visibility = View.VISIBLE
                exoPlayer.detachPlayer()// Detach player if no video
                isVideoPlaying = false
                imagePagerAdapter.submitList(property.imageUrl.toList())
            }

            // Set item click listener
            itemView.setOnClickListener { onClick(property.id!!) }

            // Check if property is liked by current user
            isLiked = viewModel.likedProperties.value?.any { it.id == property.id } ?: false
            updateLikeButtonUI(isLiked)

            // Load image list and initialize dot indicators
            val imageList = property.imageUrl.toList()
            imagePagerAdapter.submitList(imageList)
            setupDotIndicator(imageList.size)  // Initialize dot indicators
            updateDotIndicator(0) // Set default to the first image indicator
        }

            private fun setupListeners() {
            binding.likeButton.setOnClickListener {
                val propertyId = property?.id ?: return@setOnClickListener
                // Toggle like status
                viewModel.toggleLikeProperty(propertyId)
            }

            binding.commentButton.setOnClickListener {
                property?.id?.let(onCommentClick)
            }

            binding.shareButton.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this property: ${binding.propertyTitle.text}\n\n${binding.contentDescription.text}"
                    )
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Property"))
            }

            // Observe changes in liked properties to update UI
            viewModel.likedProperties.observeForever {
                val isLikedUpdated = it.any { likedProperty -> likedProperty.id == property?.id }
                updateLikeButtonUI(isLikedUpdated)
            }

            /*// Add listener for tap-to-play/pause functionality
            binding.propertyVideoPlayer.setOnClickListener {
                togglePlayPause()
            }*/
        }

        fun releaseExoPlayer() {
            exoPlayer.releasePlayer() // Let ExoPlayerManager handle the release
            isVideoPlaying = false
        }

        fun reattachExoPlayer(videoUrl: String) {
            if (binding.propertyVideoPlayer.visibility == View.VISIBLE) {
                exoPlayer.attachPlayerToView(binding.propertyVideoPlayer, videoUrl)
                isVideoPlaying = true
            }
        }

        fun playExoPlayer()
        {
            exoPlayer.resume()
            isVideoPlaying = true
        }


        private fun updateLikeButtonUI(isLiked: Boolean) {
            val color = if (isLiked) R.color.red else R.color.colorOnPrimary
            binding.likeButton.iconTint = ContextCompat.getColorStateList(context, color)
        }

        private fun setupViewPagerIndicator() {
            binding.propertyImagePager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateDotIndicator(position)
                }
            })
        }

        private fun setupDotIndicator(imageCount: Int) {
            // Set the visibility of the dot indicator based on the image count
            binding.dotIndicator.visibility = if (imageCount > 1) View.VISIBLE else View.GONE

            if (imageCount <= 1) return // Exit early if only one or no images are present

            // Remove any existing dots
            binding.dotIndicator.removeAllViews()

            // Create dot indicators based on the image count
            for (i in 0 until imageCount) {
                val dot = View(context).apply {
                    background = ContextCompat.getDrawable(
                        context,
                        if (i == 0) R.drawable.selected_dot else R.drawable.default_dot
                    )
                    layoutParams = LinearLayout.LayoutParams(10, 10).apply {
                        setMargins(8, 0, 8, 0)
                    }
                }
                binding.dotIndicator.addView(dot)
            }
        }

        private fun updateDotIndicator(position: Int) {
            val dotCount = imagePagerAdapter.currentList.size
            for (i in 0 until dotCount) {
                val dot = binding.dotIndicator.getChildAt(i)
                dot?.background = ContextCompat.getDrawable(
                    context,
                    if (i == position) R.drawable.selected_dot else R.drawable.default_dot
                )
            }
        }


        /*// Toggle play/pause for the video
        private fun togglePlayPause() {
            if (isVideoPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.resume()
            }
            isVideoPlaying = !isVideoPlaying
        }*/
    }
}

class PropertyDiffCallback : DiffUtil.ItemCallback<Property>() {
    override fun areItemsTheSame(oldItem: Property, newItem: Property) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Property, newItem: Property) = oldItem == newItem
}