package com.application.real_estate_app.feature_property.ui.uploads.fragments


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.application.real_estate_app.feature_property.R
import com.application.real_estate_app.feature_property.ui.uploads.adapters.MediaAdapter
import com.application.real_estate_app.feature_property.databinding.FragmentMediaSelectionBinding
import com.application.real_estate_app.feature_property.ui.uploads.viewModels.AddPropertyField
import com.application.real_estate_app.feature_property.ui.uploads.viewModels.AddPropertyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaSelectionFragment : Fragment(R.layout.fragment_media_selection) {
    private var _binding: FragmentMediaSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddPropertyViewModel by activityViewModels()
    private val selectedMedia: MutableList<Uri> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,

        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaSelectionBinding.inflate(inflater, container, false)

        // Set up RecyclerView adapter and Grid layout manager
        binding.mediaRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        val adapter = MediaAdapter(selectedMedia, requireContext()) // Display all selected media
        binding.mediaRecyclerView.adapter = adapter

        // Handle images/videos selection
        binding.selectImagesButton.setOnClickListener {
            openMediaPicker()
        }

        // Handle confirm button click
        binding.confirmSelectionButton.setOnClickListener {
            if (selectedMedia.isNotEmpty()) {
                val selectedImages = selectedMedia.filter { isImage(it) }
                val selectedVideos = selectedMedia.filter { isVideo(it) }
                viewModel.updateField(AddPropertyField.SelectedImageUris, selectedImages)
                viewModel.updateField(AddPropertyField.SelectedVideoUris, selectedVideos)
                // Navigate back to AddPropertyFragment
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Please select at least one media file", Toast.LENGTH_SHORT).show()
            }
        }

        // Set initial visibility
        updateConfirmButtonVisibility()

        return binding.root
    }

    // Define a launcher at the class level
    private val mediaPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                // Clear previous selections
                selectedMedia.clear()

                if (data?.clipData != null) { // Multiple media selected
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val mediaUri = data.clipData!!.getItemAt(i).uri
                        addMediaToList(mediaUri)
                    }
                } else if (data?.data != null) { // Single media selected
                    val mediaUri = data.data!!
                    addMediaToList(mediaUri)
                }
            }
        }

    // Method to open the media picker
    private fun openMediaPicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "*/*" // Allow all types (images and videos)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*")) // Specify image and video types
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        mediaPickerLauncher.launch(Intent.createChooser(intent, "Select Media"))
    }

    private fun updateConfirmButtonVisibility() {
        binding.confirmSelectionButton.visibility = if (selectedMedia.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun addMediaToList(mediaUri: Uri) {
        selectedMedia.add(mediaUri)
        binding.mediaRecyclerView.adapter?.notifyItemInserted(selectedMedia.size - 1)
        updateConfirmButtonVisibility() // Update visibility when media is added
    }


    private fun isImage(uri: Uri): Boolean {
        val type = requireContext().contentResolver.getType(uri)
        return type?.startsWith("image/") == true
    }

    private fun isVideo(uri: Uri): Boolean {
        val type = requireContext().contentResolver.getType(uri)
        return type?.startsWith("video/") == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
