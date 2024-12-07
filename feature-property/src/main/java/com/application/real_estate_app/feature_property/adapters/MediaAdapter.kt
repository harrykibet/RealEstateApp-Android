package com.application.real_estate_app.feature_property.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.application.real_estate_app.feature_property.R

class MediaAdapter(private val mediaList: List<Uri>,
                   private val context: Context)
    : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.mediaView) // ImageView for images
        val videoView: VideoView = view.findViewById(R.id.videoView) // VideoView for videos
        val mediaContainer: View = view.findViewById(R.id.mediaContainer) // Common container for both ImageView and VideoView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media_gallery, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaUri = mediaList[position]

        if (isVideo(mediaUri)) {
            holder.imageView.visibility = View.GONE
            holder.videoView.visibility = View.VISIBLE
            holder.videoView.setVideoURI(mediaUri)

            holder.videoView.setOnPreparedListener {
                it.isLooping = true
                it.start()
            }
        } else {
            holder.videoView.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE

            // Load image using Glide for better performance
            Glide.with(holder.imageView.context)
                .load(mediaUri)
                .centerCrop()
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = mediaList.size

    private fun isVideo(uri: Uri): Boolean {
        val mimeType = this.context.contentResolver.getType(uri)
        if (mimeType?.startsWith("video/") == true) {
            return true
        }

        // Fallback: check file extension
        val fileExtension = uri.toString().substringAfterLast('.', "").lowercase()
        return fileExtension in listOf("mp4", "avi", "mkv", "mov", "flv", "webm") // Extend as needed
    }

}
