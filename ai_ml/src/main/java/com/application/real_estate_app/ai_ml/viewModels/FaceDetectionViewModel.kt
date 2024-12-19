package com.application.real_estate_app.machine_learning.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.real_estate_app.machine_learning.feature.FaceDetectionProcessor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceDetectionViewModel @Inject constructor() : ViewModel() {
    private val faceProcessor = FaceDetectionProcessor()

    private val _faces = MutableLiveData<List<Face>>()
    val faces: LiveData<List<Face>> get() = _faces

    fun detectFaces(image: InputImage) {
        viewModelScope.launch {
            val result = faceProcessor.processImage(image)
            _faces.postValue(result.data as List<Face>)
        }
    }

    override fun onCleared() {
        super.onCleared()
        faceProcessor.stop()
    }
}
