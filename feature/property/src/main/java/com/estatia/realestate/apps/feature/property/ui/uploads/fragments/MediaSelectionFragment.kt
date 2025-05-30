package com.estatia.realestate.apps.feature.property.ui.uploads.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.estatia.realestate.apps.core.common.interfaces.IMediaCompressor
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.common.media.MediaFileUtils
import com.application.real_estate_app.feature_property.R
import com.application.real_estate_app.feature_property.databinding.FragmentMediaSelectionBinding
import com.estatia.realestate.apps.feature.property.ui.uploads.adapters.MediaAdapter
import com.estatia.realestate.apps.feature.property.ui.uploads.viewModels.AddPropertyField
import com.estatia.realestate.apps.feature.property.ui.uploads.viewModels.AddPropertyViewModel
import com.estatia.realestate.apps.feature.property.utils.MediaStrings
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MediaSelectionFragment : Fragment(R.layout.fragment_media_selection) {
    private var _binding: FragmentMediaSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddPropertyViewModel by activityViewModels()
    private val selectedMedia: MutableList<Uri> = mutableListOf()

    @Inject
    lateinit var logger: LoggerInterface

    @Inject
    lateinit var mediaCompressor: IMediaCompressor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaSelectionBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadInitialMedia(requireContext())

        binding.mediaFAB.setOnClickListener { openMediaPicker() }
        binding.confirmSelectionButton.setOnClickListener { processMediaSelection() }

        updateConfirmButtonVisibility()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.mediaRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.mediaRecyclerView.adapter = MediaAdapter(selectedMedia, requireContext())
    }

    private fun loadInitialMedia(context: Context) {
        val mediaAdapter = binding.mediaRecyclerView.adapter as MediaAdapter
        MediaFileUtils.loadMedia(context, selectedMedia)
        mediaAdapter.notifyDataSetChanged()
    }

    private val mediaPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleMediaSelection(result.data)
            }
        }

    private fun openMediaPicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = MediaStrings.ALL_FILES_TYPE
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(MediaStrings.IMAGE_MIME_TYPE, MediaStrings.VIDEO_MIME_TYPE))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        mediaPickerLauncher.launch(Intent.createChooser(intent, MediaStrings.MEDIA_PICKER_TITLE))
    }

    private fun handleMediaSelection(data: Intent?) {
        selectedMedia.clear()
        data?.clipData?.let { clipData ->
            repeat(clipData.itemCount) {
                addMediaToList(clipData.getItemAt(it).uri)
            }
        } ?: data?.data?.let { singleUri ->
            addMediaToList(singleUri)
        }
    }

    private fun addMediaToList(mediaUri: Uri) {
        selectedMedia.add(mediaUri)
        binding.mediaRecyclerView.adapter?.notifyItemInserted(selectedMedia.size - 1)
        updateConfirmButtonVisibility()
    }

    private fun updateConfirmButtonVisibility() {
        binding.confirmSelectionButton.visibility = if (selectedMedia.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun processMediaSelection() {
        if (selectedMedia.isEmpty()) {
            Toast.makeText(requireContext(), MediaStrings.MEDIA_SELECTION_PROMPT, Toast.LENGTH_SHORT).show()
            return
        }

        val compressedMedia = mutableListOf<Uri>()
        val outputDir = requireContext().cacheDir

        selectedMedia.forEach { mediaUri ->
            val fileSize = MediaFileUtils.getFileSize(requireContext(), mediaUri)

            when {
                fileSize == null -> {
                    logger.e("Failed to determine file size for URI: $mediaUri")
                }
                MediaFileUtils.isFileSizeExceedingLimit(fileSize) -> {
                    Toast.makeText(requireContext(), MediaStrings.FILE_SIZE_LIMIT_EXCEEDED_MESSAGE, Toast.LENGTH_SHORT).show()
                }
                MediaFileUtils.shouldCompress(fileSize) -> {
                    compressMedia(mediaUri, outputDir, compressedMedia)
                }
                else -> {
                    compressedMedia.add(mediaUri)
                    checkAllMediaProcessed(compressedMedia)
                }
            }
        }
    }

    private fun compressMedia(mediaUri: Uri, outputDir: File, compressedMedia: MutableList<Uri>) {
        if (MediaFileUtils.isVideo(requireContext(), mediaUri)) {
            mediaCompressor.compressVideo(requireContext(), mediaUri, outputDir) { compressedFile ->
                handleCompressedFile(compressedFile, compressedMedia)
            }
        } else {
            val compressedFile = mediaCompressor.compressImage(requireContext(), mediaUri, outputDir)
            handleCompressedFile(compressedFile, compressedMedia)
        }
    }

    private fun handleCompressedFile(compressedFile: File?, compressedMedia: MutableList<Uri>) {
        compressedFile?.let {
            compressedMedia.add(Uri.fromFile(it))
        }
        checkAllMediaProcessed(compressedMedia)
    }

    private fun checkAllMediaProcessed(compressedMedia: List<Uri>) {
        if (compressedMedia.size == selectedMedia.size) {
            val compressedImages = compressedMedia.filter { MediaFileUtils.isImage(requireContext(), it) }
            val compressedVideos = compressedMedia.filter { MediaFileUtils.isVideo(requireContext(), it) }

            viewModel.updateField(AddPropertyField.SelectedImageUris, compressedImages)
            viewModel.updateField(AddPropertyField.SelectedVideoUris, compressedVideos)

            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
