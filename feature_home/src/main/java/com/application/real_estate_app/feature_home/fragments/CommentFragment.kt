package com.application.real_estate_app.feature_home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.real_estate_app.feature_home.R
import com.application.real_estate_app.feature_home.adapters.CommentAdapter
import com.application.real_estate_app.feature_home.databinding.FragmentCommentBinding
import com.application.real_estate_app.domain.models.Comment
import com.application.real_estate_app.feature_home.viewModels.PropertyViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class CommentFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!
    private var commentAdapter: CommentAdapter? = null

    private val propertyViewModel: PropertyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve propertyId from fragment arguments
        val args: CommentFragmentArgs by navArgs()
        val propertyId = args.propertyId
        val userId = args.userId

        // Set up RecyclerView
        commentAdapter = CommentAdapter()
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }

        // Start listening for comments
        propertyViewModel.startListeningForComments(propertyId)

        propertyViewModel.comments.observe(viewLifecycleOwner) { comments ->
            if (isAdded) {
                commentAdapter?.updateComments(comments)
                binding.noCommentsPlaceholder.visibility =
                    if (comments.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        propertyViewModel.noCommentsPlaceholderVisibility.observe(viewLifecycleOwner) { visibility ->
            if (isAdded) {
                binding.noCommentsPlaceholder.visibility =
                    visibility // Set visibility for the placeholder TextView
            }
        }

        propertyViewModel.commentSubmitStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isAdded) {
                if (isSuccess) {
                    Toast.makeText(requireContext(), "Comment submitted!", Toast.LENGTH_SHORT).show()
                    binding.commentEditText.text?.clear()
                } else {
                    Toast.makeText(requireContext(), "Failed to submit comment", Toast.LENGTH_SHORT).show()
                }
                binding.sendCommentButton.isEnabled = true
            }
        }

        // Handle comment submission
        binding.sendCommentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString().trim()
            if (commentText.isNotEmpty()) {
                binding.sendCommentButton.isEnabled = false
                val comment = Comment(id = null,
                    userId = userId,
                    commentText = commentText,
                    timeStamp = Date()
                )

                propertyViewModel.submitComment(propertyId, comment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        dialog?.behavior?.peekHeight = 600 // Set a default peek height as desired
        dialog?.behavior?.isFitToContents = true
        dialog?.behavior?.isHideable = false

        // Set dim amount programmatically
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setDimAmount(0.5f) // Adjust the dim amount here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        commentAdapter = null
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }
}