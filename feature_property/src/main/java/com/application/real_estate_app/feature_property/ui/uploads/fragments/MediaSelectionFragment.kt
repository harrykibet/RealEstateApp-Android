package com.application.real_estate_app.feature_property.ui.uploads.fragments

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.application.real_estate_app.feature_property.R
import com.application.real_estate_app.feature_property.databinding.FragmentMediaSelectionBinding
import com.application.real_estate_app.feature_property.ui.uploads.adapters.MediaAdapter
import com.application.real_estate_app.feature_property.ui.uploads.viewModels.AddPropertyField
import com.application.real_estate_app.feature_property.ui.uploads.viewModels.AddPropertyViewModel
import com.application.real_estate_app.core.utils.compression.MediaCompressor
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.feature_property.utils.MediaStrings
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MediaSelectionFragment : Fragment(R.layout.fragment_media_selection) {
    private var _binding: FragmentMediaSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddPropertyViewModel by activityViewModels() //shared ViewModel
    private val selectedMedia: MutableList<Uri> = mutableListOf()

    @Inject
    lateinit var logger: LoggerInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaSelectionBinding.inflate(inflater, container, false)

        binding.mediaRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        val adapter = MediaAdapter(selectedMedia, requireContext())
        binding.mediaRecyclerView.adapter = adapter

        // Load initial media
        loadInitialMedia(requireContext(), adapter)

        binding.mediaFAB.setOnClickListener {
            openMediaPicker()
        }

        binding.confirmSelectionButton.setOnClickListener {
            if (selectedMedia.isNotEmpty()) {
                compressAndUploadMedia()
            } else {
                Toast.makeText(requireContext(), MediaStrings.MEDIA_SELECTION_PROMPT, Toast.LENGTH_SHORT).show()
            }
        }

        updateConfirmButtonVisibility()
        return binding.root
    }

    private fun loadInitialMedia(context: Context, mediaAdapter: MediaAdapter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above (Scoped Storage)
            loadMediaUsingMediaStore(context, mediaAdapter)
        } else {
            // For Android 9 and below (Legacy External Storage)
            loadMediaFromExternalStorage(mediaAdapter)
        }
    }

    private fun loadMediaUsingMediaStore(context: Context, mediaAdapter: MediaAdapter) {
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Video.Media._ID)
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val queryUri = MediaStore.Files.getContentUri(MediaStrings.EXTERNAL_FILES_URI)
        val cursor = context.contentResolver.query(queryUri, projection, selection, selectionArgs, null)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(queryUri, id)
                selectedMedia.add(contentUri)
            }
        }

        mediaAdapter.notifyDataSetChanged()
    }

    private fun loadMediaFromExternalStorage(mediaAdapter: MediaAdapter) {
        val defaultPicturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        if (defaultPicturesDir != null && defaultPicturesDir.exists()) {
            val files = defaultPicturesDir.listFiles { file ->
                file.isFile && (file.extension.equals(MediaStrings.FileExtensions.JPG, true) ||
                        file.extension.equals(MediaStrings.FileExtensions.JPEG, true) ||
                        file.extension.equals(MediaStrings.FileExtensions.PNG, true) ||
                        file.extension.equals(MediaStrings.FileExtensions.MP4, true))
            }

            if (!files.isNullOrEmpty()) {
                selectedMedia.addAll(files.map { it.toUri() })
                mediaAdapter.notifyDataSetChanged()
            }
        } else {
            logger.e(MediaStrings.ERROR_PICTURES_DIR)
        }
    }

    private val mediaPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                selectedMedia.clear()
                if (data?.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val mediaUri = data.clipData!!.getItemAt(i).uri
                        addMediaToList(mediaUri)
                    }
                } else if (data?.data != null) {
                    val mediaUri = data.data!!
                    addMediaToList(mediaUri)
                }
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

    private fun updateConfirmButtonVisibility() {
        binding.confirmSelectionButton.visibility = if (selectedMedia.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun addMediaToList(mediaUri: Uri) {
        selectedMedia.add(mediaUri)
        binding.mediaRecyclerView.adapter?.notifyItemInserted(selectedMedia.size - 1)
        updateConfirmButtonVisibility()
    }

    private fun compressAndUploadMedia() {
        val compressedMedia = mutableListOf<Uri>()
        val outputDir = requireContext().cacheDir

        selectedMedia.forEach { mediaUri ->
            val fileSize = getFileSize(requireContext(), mediaUri)
            if (fileSize != null) {
                if (MediaCompressor.isFileSizeExceedingLimit(fileSize)) {
                    // Reject files larger than 50MB
                    Toast.makeText(requireContext(), MediaStrings.FILE_SIZE_LIMIT_EXCEEDED_MESSAGE, Toast.LENGTH_SHORT).show()
                } else if (MediaCompressor.shouldCompress(fileSize)) {
                    if (isVideo(mediaUri)) {
                        MediaCompressor.compressVideo(requireContext(), mediaUri, outputDir) { compressedFile ->
                            handleCompressedFile(compressedFile, compressedMedia)
                        }
                    } else {
                        val compressedFile = MediaCompressor.compressImage(requireContext(), mediaUri, outputDir)
                        handleCompressedFile(compressedFile, compressedMedia)
                    }
                } else {
                    compressedMedia.add(mediaUri)
                    checkAllMediaCompressed(compressedMedia)
                }
            }
        }
    }

    private fun handleCompressedFile(compressedFile: File?, compressedMedia: MutableList<Uri>) {
        if (compressedFile != null) {
            compressedMedia.add(Uri.fromFile(compressedFile))
        }
        checkAllMediaCompressed(compressedMedia)
    }

    private fun getFileSize(context: Context, uri: Uri): Long? {
        val descriptor = context.contentResolver.openFileDescriptor(uri, MediaStrings.FILE_READ_MODE)
        return descriptor?.use {
            it.statSize
        }
    }


    private fun checkAllMediaCompressed(compressedMedia: List<Uri>) {
        if (compressedMedia.size == selectedMedia.size) {
            val compressedImages = compressedMedia.filter { isImage(it) }
            val compressedVideos = compressedMedia.filter { isVideo(it) }
            viewModel.updateField(AddPropertyField.SelectedImageUris, compressedImages)
            viewModel.updateField(AddPropertyField.SelectedVideoUris, compressedVideos)
            findNavController().navigateUp()
        }
    }

    private fun isImage(uri: Uri): Boolean {
        val type = requireContext().contentResolver.getType(uri)
        return type?.startsWith(MediaStrings.MEDIA_TYPE_IMAGE) == true
    }

    private fun isVideo(uri: Uri): Boolean {
        val type = requireContext().contentResolver.getType(uri)
        return type?.startsWith(MediaStrings.MEDIA_TYPE_VIDEO) == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
