package com.estatia.realestate.apps.feature.property.ui.uploads.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.intelligence.IMediaIntelligenceService
import com.estatia.realestate.apps.feature.property.utils.AddPropertyDraft
import com.estatia.realestate.apps.feature.property.utils.AddPropertyUiState
import com.estatia.realestate.apps.feature.property.utils.PropertyData
import com.estatia.realestate.apps.feature.property.utils.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val repository: IPropertyRepository,
    private val authRepository: IAuthRepository,
    private val intelligenceService: IMediaIntelligenceService,
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
        AddPropertyDraft()
    )

    val draft: StateFlow<AddPropertyDraft> =
        _draft.asStateFlow()

    val allMedia: StateFlow<List<Uri>> = _draft.map {
        it.images + it.videos
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
                images = images
            )
        }
    }



    fun updateVideos(
        videos: List<Uri>
    ) {

        updateDraft {
            copy(
                videos = videos
            )
        }
    }

    fun addImage(uri: Uri) {
        updateDraft {
            copy(images = images + uri)
        }
        analyzeImage(uri)
    }

    private fun analyzeImage(uri: Uri) {
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
            copy(videos = videos + uri)
        }
    }

    fun removeMedia(uri: Uri) {
        updateDraft {
            copy(
                images = images - uri,
                videos = videos - uri
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
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            onFailure(AuthException.UserNotAuthenticated)
            return
        }

        viewModelScope.launch {
            try {
                val property = _draft.value.toDomain(userId = userId)

                when (val result = repository.uploadProperty(
                    property = property,
                    imageUris = _draft.value.images,
                    videoUris = _draft.value.videos
                )) {
                    is AppResult.Success -> onSuccess(result.data)
                    is AppResult.Error -> onFailure(result.exception)
                }
            } catch (exception: Exception) {
                onFailure(exception)
            }
        }
    }



    fun clearDraft() {

        _draft.value =
            AddPropertyDraft()

    }

}
