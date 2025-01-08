package com.application.real_estate_app.feature_favorites.ui.adapters

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.application.real_estate_app.ui_components.R.drawable.*
import com.application.real_estate_app.feature_favorites.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

class ImagePagerAdapter : ListAdapter<String, ImagePagerAdapter.ImageViewHolder>(ImageDiffCallback()) {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_property, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = getItem(position)

        // Load image and get its dimensions
        //TODO("Replace glide placeholders with animations for enhanced UX")
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    // Check if resource is a BitmapDrawable to avoid ClassCastException
                    if (resource is BitmapDrawable) {
                        val bitmap = resource.bitmap
                        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                        val newHeight = (holder.itemView.width / aspectRatio).toInt()

                        // Update the ImageView height based on the aspect ratio
                        val layoutParams = holder.imageView.layoutParams
                        layoutParams.height = newHeight
                        holder.imageView.layoutParams = layoutParams
                    }
                    return false
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(loading_image)
            .downsample(DownsampleStrategy.AT_LEAST)
            .apply(RequestOptions.centerCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(image_loading_error)
            .into(holder.imageView)
            .clearOnDetach()
    }
}

// DiffUtil for efficient list updates
class ImageDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
}
