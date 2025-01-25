package com.application.real_estate_app.feature_property.ui.uploads.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.application.real_estate_app.core.common.misc.Consts
import com.bumptech.glide.Glide
import com.application.real_estate_app.feature_property.R
import com.application.real_estate_app.feature_property.utils.MediaStrings

// Initialize the layout with media items from the gallery
// Add select and delete logic for media items displayed

class MediaAdapter(private val mediaList: List<Uri>,
                   private val context: Context)
    : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.mediaView) // ImageView for images
        val videoView: VideoView = view.findViewById(R.id.videoView) // VideoView for videos
        //val mediaContainer: View = view.findViewById(R.id.mediaContainer) // Common container for both ImageView and VideoView
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
        if (mimeType?.startsWith(MediaStrings.MEDIA_TYPE_VIDEO) == true) {
            return true
        }

        // Fallback: check file extension
        val fileExtension = uri.toString().substringAfterLast(
            MediaStrings.URI_FILE_EXTENSION_DELIMITER, Consts.EMPTY_STRING).lowercase()

        return fileExtension in listOf(
            MediaStrings.FileExtensions.MP4,
            MediaStrings.FileExtensions.MKV,
            MediaStrings.FileExtensions.AVI,
            MediaStrings.FileExtensions.MOV,
            MediaStrings.FileExtensions.WEBM,
            MediaStrings.FileExtensions.FLV) // Extend as needed
    }

}
