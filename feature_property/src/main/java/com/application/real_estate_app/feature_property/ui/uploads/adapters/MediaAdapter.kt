package com.application.real_estate_app.feature_property.ui.uploads.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.application.real_estate_app.core.utils.media.MediaFileUtils
import com.bumptech.glide.Glide
import com.application.real_estate_app.feature_property.R

class MediaAdapter(
    private val mediaList: List<Uri>,
    private val context: Context
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.mediaView) // ImageView for images
        val videoView: VideoView = view.findViewById(R.id.videoView) // VideoView for videos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media_gallery, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaUri = mediaList[position]

        if (MediaFileUtils.isVideo(context, mediaUri)) {
            // Handle Video
            holder.imageView.visibility = View.GONE
            holder.videoView.visibility = View.VISIBLE
            holder.videoView.setVideoURI(mediaUri)

            holder.videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                mediaPlayer.setVolume(0f, 0f) // Mute video preview
                mediaPlayer.start()
            }

        } else {
            // Handle Image
            holder.videoView.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE

            Glide.with(holder.imageView.context)
                .load(mediaUri)
                .centerCrop()
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = mediaList.size
}
