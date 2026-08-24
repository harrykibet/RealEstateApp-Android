package com.estatia.realestate.apps.feature.property.ui.uploads.viewModels

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.intelligence.IMediaIntelligenceService
import com.estatia.realestate.apps.feature.property.utils.AddPropertyDraft
import com.estatia.realestate.apps.feature.property.utils.AddPropertyUiState
import com.estatia.realestate.apps.feature.property.utils.PropertyData
import com.estatia.realestate.apps.feature.property.utils.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STATE_DRAFT = "property_draft"

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val repository: IPropertyRepository,
    private val authRepository: IAuthRepository,
    private val intelligenceService: IMediaIntelligenceService,
    private val metricsTracker: IMetricsTracker,
    private val savedStateHandle: SavedStateHandle,
    propertyData: PropertyData
) : ViewModel() {


    private val _uiState = MutableStateFlow(
        AddPropertyUiState(
            countyNames = propertyData.counties,
            propertyTypes = propertyData.propertyTypes
        )
    )

    val uiState: StateFlow<AddPropertyUiState> =
        _uiState.asStateFlow()



    private val _draft = MutableStateFlow(
        savedStateHandle.get<AddPropertyDraft>(STATE_DRAFT) ?: AddPropertyDraft()
    )

    val draft: StateFlow<AddPropertyDraft> =
        _draft.asStateFlow()

    init {
        // 🛡️ State Restoration: Persist draft changes
        _draft.onEach { draft ->
            savedStateHandle[STATE_DRAFT] = draft
        }.launchIn(viewModelScope)
    }

    val allMedia: StateFlow<List<Uri>> = _draft.map {
        (it.images + it.videos).map { ref -> ref.value.toUri() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    /**
     * Generic immutable state update.
     */
    private fun updateDraft(
        update: AddPropertyDraft.() -> AddPropertyDraft
    ) {
        _draft.update {
            it.update()
        }
    }



    fun updateTitle(value: String) {

        updateDraft {
            copy(
                title = value
            )
        }
    }



    fun updateDescription(value: String?) {

        updateDraft {
            copy(
                description = value
            )
        }
    }



    fun updatePrice(value: Double?) {

        updateDraft {
            copy(
                price = value
            )
        }
    }



    fun updateDepositAmount(value: Double?) {

        updateDraft {
            copy(
                depositAmount = value
            )
        }
    }



    fun updateCounty(value: String?) {

        updateDraft {
            copy(
                county = value
            )
        }
    }



    fun updatePropertyType(value: String?) {

        updateDraft {
            copy(
                propertyType = value
            )
        }
    }



    fun updateBedrooms(value: Int?) {

        updateDraft {
            copy(
                bedrooms = value
            )
        }
    }



    fun updateBathrooms(value: Int?) {

        updateDraft {
            copy(
                bathrooms = value
            )
        }
    }



    fun updateAreaSize(value: Double?) {

        updateDraft {
            copy(
                areaSize = value
            )
        }
    }



    fun updateFeatures(value: String?) {

        updateDraft {
            copy(
                features = value
            )
        }
    }



    fun updateAddress(value: String?) {

        updateDraft {
            copy(
                address = value
            )
        }
    }



    fun updateAvailableFrom(value: String?) {

        updateDraft {
            copy(
                availableFrom = value
            )
        }
    }



    fun updateLeaseTerms(value: String?) {

        updateDraft {
            copy(
                leaseTerms = value
            )
        }
    }



    fun updateLocation(
        latitude: Double?,
        longitude: Double?
    ) {

        updateDraft {
            copy(
                latitude = latitude,
                longitude = longitude
            )
        }
    }



    fun updateContact(
        email: String?,
        phone: String?
    ) {

        updateDraft {
            copy(
                contactEmail = email,
                contactPhone = phone
            )
        }
    }



    fun updateImages(
        images: List<Uri>
    ) {

        updateDraft {
            copy(
                images = images.map { MediaReference(it.toString()) }
            )
        }
    }



    fun updateVideos(
        videos: List<Uri>
    ) {

        updateDraft {
            copy(
                videos = videos.map { MediaReference(it.toString()) }
            )
        }
    }

    fun addImage(uri: Uri) {
        val ref = MediaReference(uri.toString())
        updateDraft {
            copy(images = images + ref)
        }
        analyzeImage(ref)
    }

    private fun analyzeImage(uri: MediaReference) {
        viewModelScope.launch {
            try {
                val detectedAmenities = intelligenceService.extractAmenities(uri)
                updateDraft {
                    copy(amenities = amenities + detectedAmenities)
                }
            } catch (e: Exception) {
                // Silently fail for intelligence features to not block UI
            }
        }
    }

    fun addVideo(uri: Uri) {
        updateDraft {
            copy(videos = videos + MediaReference(uri.toString()))
        }
    }

    fun removeMedia(uri: Uri) {
        val refValue = uri.toString()
        updateDraft {
            copy(
                images = images.filter { it.value != refValue },
                videos = videos.filter { it.value != refValue }
            )
        }
    }



    fun toggleAmenity(
        amenity: String
    ) {

        updateDraft {

            val updated =
                if (amenity in amenities)
                    amenities - amenity
                else
                    amenities + amenity


            copy(
                amenities = updated
            )
        }
    }



    fun saveProperty(
        onFailure: (Exception) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        if (_uiState.value.isUploading) return // 🛡️ Idempotency: Prevent duplicate uploads

        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            onFailure(AuthException.UserNotAuthenticated)
            return
        }

        _uiState.update { it.copy(isUploading = true) }

        viewModelScope.launch {
            try {
                val property = _draft.value.toDomain(userId = userId)

                when (val result = repository.uploadProperty(
                    property = property,
                    imageUris = _draft.value.images,
                    videoUris = _draft.value.videos
                )) {
                    is AppResult.Success -> {
                        metricsTracker.incrementCounter("property.draft.completed")
                        _uiState.update { it.copy(isUploading = false) }
                        onSuccess(result.data)
                    }
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isUploading = false) }
                        onFailure(result.exception)
                    }
                }
            } catch (exception: Exception) {
                _uiState.update { it.copy(isUploading = false) }
                onFailure(exception)
            }
        }
    }

    fun markDraftAbandoned() {
        metricsTracker.incrementCounter("property.draft.abandoned")
    }



    fun clearDraft() {

        _draft.value =
            AddPropertyDraft()

    }

}
