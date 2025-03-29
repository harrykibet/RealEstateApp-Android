package com.application.real_estate_app.feature_comments.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.application.real_estate_app.core_common.misc.Consts
import com.application.real_estate_app.core_model.Comment
import com.application.real_estate_app.core_model.User
import com.application.real_estate_app.feature_comments.R
import com.application.real_estate_app.feature_comments.databinding.ItemCommentBinding
import com.application.real_estate_app.feature_comments.ui.viewmodels.CommentsViewModel
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter (
    private var commentList: List<Comment?>,
    private val viewModel: CommentsViewModel
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]

        viewModel.getUser(comment?.userId!!) { user ->
            holder.bind(comment, user)
        }
    }
    override fun getItemCount(): Int = commentList.size

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Update to accept a nullable User and handle nullable timeStamp
        fun bind(comment: Comment, user: User?) {
            binding.commentText.text = comment.commentText
            binding.commentUsername.text = user?.name ?: itemView.context.getString(R.string.unknown_user)
            loadProfilePicture(user?.profilePictureUrl)

            // Format and display the timestamp if available, otherwise show placeholder text
            val formattedTimestamp = comment.timeStamp?.let { formatTimestamp(it) }
                ?: itemView.context.getString(R.string.pending_timestamp)
            binding.commentTimestamp.text = formattedTimestamp
        }

        // Simplified Glide profile picture loading with default placeholder
        private fun loadProfilePicture(profilePicUrl: String?) {
            Glide.with(binding.commentProfileImage.context)
                .load(profilePicUrl ?: R.drawable.circle_profile_placeholder) // Load URL or fallback to placeholder
                .circleCrop()
                .into(binding.commentProfileImage)
        }
    }

    fun updateComments(newComments: List<Comment?>) {
        val diffResult = DiffUtil.calculateDiff(CommentDiffCallback(commentList, newComments))

        synchronized(this) {
            commentList = newComments.toList() // Ensure immutability
        }

        diffResult.dispatchUpdatesTo(this)
    }

    private fun formatTimestamp(timestamp: Date): String {
        val dateFormat = SimpleDateFormat(Consts.DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(timestamp) // Format the date and return as String
    }
}

class CommentDiffCallback(
    private val oldList: List<Comment?>,
    private val newList: List<Comment?>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]!!.id == newList[newItemPosition]!!.id // Assume each comment has a unique ID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}