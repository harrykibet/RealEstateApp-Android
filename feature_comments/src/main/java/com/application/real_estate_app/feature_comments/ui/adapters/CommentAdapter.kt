package com.application.real_estate_app.feature_comments.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.application.real_estate_app.core.common.misc.Consts
import com.application.real_estate_app.core.data.db_names.FirestoreCollections
import com.application.real_estate_app.core.domain.models.Comment
import com.application.real_estate_app.core.domain.models.User
import com.application.real_estate_app.feature_comments.R
import com.application.real_estate_app.feature_comments.databinding.ItemCommentBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var commentList = mutableListOf<Comment?>()
    private val userCache = mutableMapOf<String?, User>() // Cache to store user data
    private val firestore = FirebaseFirestore.getInstance() // Initialize once for FireStore instance

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]

        // Check cache for user data
        //TODO("Use viewModel to check cache for user data")
        if (userCache.containsKey(comment?.userId)) {
            holder.bind(comment!!, userCache[comment.userId])
        } else {
            // Fetch user data from FireStore if not cached
            firestore.collection(FirestoreCollections.USERS)
                .document(comment!!.userId!!)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        userCache[comment.userId] = user // Cache the user data
                        holder.bind(comment, user)
                    } else {
                        holder.bind(comment, null) // Pass null if user data is not found
                    }
                }
                .addOnFailureListener {
                    holder.bind(comment, null) // Handle failure by binding with null user
                }
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

    // Method to update comments using DiffUtil for efficient RecyclerView updates
    fun updateComments(newComments: List<Comment?>) {
        val diffCallback = CommentDiffCallback(commentList, newComments)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // Update the list after diff calculation
        commentList = newComments.toMutableList()
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