package com.application.real_estate_app.feature_property.ui.uploads.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.real_estate_app.domain.interfaces.AuthRepository
import com.application.real_estate_app.domain.interfaces.IPropertyRepository
import com.application.real_estate_app.domain.models.Property
import com.application.real_estate_app.feature_property.data.utils.PropertyData
import com.application.real_estate_app.feature_property.data.utils.AddPropertyUiState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val propertyRepository: IPropertyRepository,
    propertyData: PropertyData,
    authChecker: AuthRepository
) : ViewModel() {

    private val userId = authChecker.getCurrentUserId()

    // MutableStateFlow for individual fields
    private val _isUpLoading = MutableStateFlow(propertyRepository.uploadStatus.value)
    val isUpLoading: StateFlow<Boolean?> = _isUpLoading.asStateFlow()

    private val _uploadingError = MutableStateFlow(propertyRepository.uploadError.value)
    val uploadingError: StateFlow<String?> = _uploadingError.asStateFlow()

    private val _title = MutableStateFlow<String?>(null)
    val title: StateFlow<String?> = _title.asStateFlow()

    private val _description = MutableStateFlow<String?>(null)
    val description: StateFlow<String?> = _description.asStateFlow()

    private val _price = MutableStateFlow<Double?>(null)
    val price: StateFlow<Double?> = _price.asStateFlow()

    private val _contactEmail = MutableStateFlow<String?>(null)
    val contactEmail: StateFlow<String?> = _contactEmail.asStateFlow()

    private val _contactPhone = MutableStateFlow<String?>(null)
    val contactPhone: StateFlow<String?> = _contactPhone.asStateFlow()

    private val _wifiChecked = MutableStateFlow(false)
    val wifiChecked: StateFlow<Boolean> = _wifiChecked.asStateFlow()

    private val _poolChecked = MutableStateFlow(false)
    val poolChecked: StateFlow<Boolean> = _poolChecked.asStateFlow()

    private val _gymChecked = MutableStateFlow(false)
    val gymChecked: StateFlow<Boolean> = _gymChecked.asStateFlow()

    private val _parkingChecked = MutableStateFlow(false)
    val parkingChecked: StateFlow<Boolean> = _parkingChecked.asStateFlow()

    private val _airConditioningChecked = MutableStateFlow(false)
    val airConditioningChecked: StateFlow<Boolean> = _airConditioningChecked.asStateFlow()

    private val _securityChecked = MutableStateFlow(false)
    val securityChecked: StateFlow<Boolean> = _securityChecked.asStateFlow()

    private val _depositAmount = MutableStateFlow<Double?>(null)
    val depositAmount: StateFlow<Double?> = _depositAmount.asStateFlow()

    private val _county = MutableStateFlow<String?>(null)
    val county: StateFlow<String?> = _county.asStateFlow()

    private val _propertyType = MutableStateFlow<String?>(null)
    val propertyType: StateFlow<String?> = _propertyType.asStateFlow()

    private val _bedrooms = MutableStateFlow<Int?>(null)
    val bedrooms: StateFlow<Int?> = _bedrooms.asStateFlow()

    private val _bathrooms = MutableStateFlow<Int?>(null)
    val bathrooms: StateFlow<Int?> = _bathrooms.asStateFlow()

    private val _areaSize = MutableStateFlow<Double?>(null)
    val areaSize: StateFlow<Double?> = _areaSize.asStateFlow()

    private val _amenities = MutableStateFlow(listOf<String>())
    //val amenities: StateFlow<List<String>> = _amenities.asStateFlow()

    private val _availableFrom = MutableStateFlow<String?>(null)
    val availableFrom: StateFlow<String?> = _availableFrom.asStateFlow()

    private val _leaseTerms = MutableStateFlow<String?>(null)
    val leaseTerms: StateFlow<String?> = _leaseTerms.asStateFlow()

    private val _additionalFeatures = MutableStateFlow<String?>(null)
    val additionalFeatures: StateFlow<String?> = _additionalFeatures.asStateFlow()

    private val _propertyLocation = MutableStateFlow<LatLng?>(null)
    //val propertyLocation: StateFlow<LatLng?> = _propertyLocation.asStateFlow()

    // StateFlow to hold UI state
    private val _uiState = MutableStateFlow(AddPropertyUiState())
    //val uiState: StateFlow<AddPropertyUiState> = _uiState.asStateFlow()

    val countyNames = MutableStateFlow(propertyData.counties)
    val propertyTypes = MutableStateFlow(propertyData.propertyTypes)

    init {
        // Initialize county names and property types
        _uiState.update { it.copy(
            countyNames = propertyData.counties,
            propertyTypes = propertyData.propertyTypes
        ) }
    }


    // General update method for fields
    @Suppress("UNCHECKED_CAST")
    fun <T> updateField(field: AddPropertyField, value: T) {
        when (field) {
            AddPropertyField.Title -> {
                val newTitle = value as String
                if (newTitle.isNotEmpty() && _title.value != newTitle)
                {
                    _uiState.update { it.copy(title = newTitle) }
                    _title.value = newTitle
                }
            }

            AddPropertyField.Description -> {
                val newDescription = value as String
                if (newDescription.isNotEmpty() && _description.value != newDescription)
                {
                    _uiState.update { it.copy(description = newDescription) }
                    _description.value = newDescription
                }
            }

            AddPropertyField.Price -> {
                val newPrice = value as Double
                if (newPrice > 0 && _price.value != newPrice)
                {
                    _uiState.update { it.copy(price = newPrice) }
                    _price.value = newPrice
                }
            }

            AddPropertyField.DepositAmount -> {
                val newDepositAmount = value as Double
                if (newDepositAmount > 0 && _depositAmount.value != newDepositAmount)
                {
                    _uiState.update { it.copy(depositAmount = newDepositAmount) }
                    _depositAmount.value = newDepositAmount
                }
            }

            AddPropertyField.County -> {
                val newCounty = value as String
                if (newCounty.isNotEmpty() && _county.value != newCounty)
                {
                    _uiState.update { it.copy(county = newCounty) }
                    _county.value = newCounty
                }
            }

            AddPropertyField.PropertyType -> {
                val newPropertyType = value as String
                if (newPropertyType.isNotEmpty() && _propertyType.value != newPropertyType)
                {
                    _uiState.update { it.copy(propertyType = newPropertyType) }
                    _propertyType.value = newPropertyType
                }
            }

            AddPropertyField.Bedrooms -> {
                val newBedrooms = value as Int
                if (newBedrooms > 0 && _bedrooms.value != newBedrooms)
                {
                    _uiState.update { it.copy(bedrooms = newBedrooms) }
                    _bedrooms.value = newBedrooms
                }
            }

            AddPropertyField.Bathrooms -> {
                val newBathrooms = value as Int
                if (newBathrooms > 0 && _bathrooms.value != newBathrooms)
                {
                    _uiState.update { it.copy(bathrooms = newBathrooms) }
                    _bathrooms.value = newBathrooms
                }
            }

            AddPropertyField.AreaSize -> {
                val newAreaSize = value as Double
                if (newAreaSize > 0 && _areaSize.value != newAreaSize)
                {
                    _uiState.update { it.copy(areaSize = newAreaSize) }
                    _areaSize.value = newAreaSize
                }
            }

            AddPropertyField.Amenities -> {
                val newAmenities = value as List<String>
                if (newAmenities.isNotEmpty() && _amenities.value != newAmenities)
                {
                    _uiState.update { it.copy(amenities = newAmenities) }
                    _amenities.value = newAmenities
                }
            }

            AddPropertyField.AvailableFrom -> {
                val newAvailableFrom = value as String
                if (newAvailableFrom.isNotEmpty() && _availableFrom.value != newAvailableFrom)
                {
                    _uiState.update { it.copy(availableFrom = newAvailableFrom) }
                    _availableFrom.value = newAvailableFrom
                }
            }

            AddPropertyField.LeaseTerms -> {
                val newLeaseTerms = value as String
                if (newLeaseTerms.isNotEmpty() && _leaseTerms.value != newLeaseTerms)
                {
                    _uiState.update { it.copy(leaseTerms = newLeaseTerms) }
                    _leaseTerms.value = newLeaseTerms
                }
            }

            AddPropertyField.AdditionalFeatures -> {
                val newAdditionalFeatures = value as String
                if (newAdditionalFeatures.isNotEmpty() && _additionalFeatures.value != newAdditionalFeatures)
                {
                    _uiState.update { it.copy(additionalFeatures = newAdditionalFeatures) }
                    _additionalFeatures.value = newAdditionalFeatures
                }
            }

            AddPropertyField.SelectedImageUris -> {
                _uiState.update { it.copy(selectedImageUris = value as MutableList<Uri>) }
            }

            AddPropertyField.SelectedVideoUris -> {
                _uiState.update { it.copy(selectedVideoUris = value as MutableList<Uri>) }
            }

            AddPropertyField.PropertyLocation -> {
                _uiState.update { it.copy(propertyLocation = value as LatLng?) }
                _propertyLocation.value = value as LatLng?
            }

            AddPropertyField.ContactEmail -> {
                val newContactEmail = value as String
                if (newContactEmail.isNotEmpty() && _contactEmail.value != newContactEmail)
                {
                    _uiState.update { it.copy(contactEmail = newContactEmail) }
                    _contactEmail.value = newContactEmail
                }
            }
            AddPropertyField.ContactPhone -> {
                val newContactPhone = value as String
                if (newContactPhone.isNotEmpty() && _contactPhone.value != newContactPhone)
                {
                    _uiState.update { it.copy(contactPhone = newContactPhone) }
                    _contactPhone.value = newContactPhone
                }
            }
            AddPropertyField.WifiChecked -> {
                val newWifiChecked = value as Boolean
                if (newWifiChecked != _wifiChecked.value) {
                    _uiState.update {
                        it.copy(wifiChecked = newWifiChecked).also { updateAmenitiesList() }
                    }
                    _wifiChecked.value = newWifiChecked
                }
            }
            AddPropertyField.PoolChecked -> {
                val newPoolChecked = value as Boolean
                if (newPoolChecked != _poolChecked.value) {
                    _uiState.update {
                        it.copy(poolChecked = newPoolChecked).also { updateAmenitiesList() }
                    }
                    _poolChecked.value = newPoolChecked
                }
            }
            AddPropertyField.GymChecked -> {
                val newGymChecked = value as Boolean
                if (newGymChecked != _gymChecked.value) {
                    _uiState.update {
                        it.copy(gymChecked = newGymChecked).also { updateAmenitiesList() }
                    }
                    _gymChecked.value = newGymChecked
                }
            }
            AddPropertyField.ParkingChecked -> {
                val newParkingChecked = value as Boolean
                if (newParkingChecked != _parkingChecked.value) {
                    _uiState.update {
                        it.copy(parkingChecked = newParkingChecked).also { updateAmenitiesList() }
                    }
                    _parkingChecked.value = newParkingChecked
                }
            }
            AddPropertyField.AirConditioningChecked -> {
                val newAirConditioningChecked = value as Boolean
                if (newAirConditioningChecked != _airConditioningChecked.value) {
                    _uiState.update {
                        it.copy(airConditioningChecked = newAirConditioningChecked).also { updateAmenitiesList() }
                    }
                    _airConditioningChecked.value = newAirConditioningChecked
                }
            }
            AddPropertyField.SecurityChecked -> {
                val newSecurityChecked = value as Boolean
                if (newSecurityChecked != _securityChecked.value) {
                    _uiState.update {
                        it.copy(securityChecked = newSecurityChecked).also { updateAmenitiesList() }
                    }
                    _securityChecked.value = newSecurityChecked
                }
            }
        }
    }

    // Update amenities based on checkbox states
    private fun updateAmenitiesList() {
        val currentState = _uiState.value
        val amenitiesList = mutableListOf<String>()
        if (currentState.wifiChecked) amenitiesList.add("Wi-Fi")
        if (currentState.poolChecked) amenitiesList.add("Pool")
        if (currentState.gymChecked) amenitiesList.add("Gym")
        if (currentState.parkingChecked) amenitiesList.add("Parking")
        if (currentState.airConditioningChecked) amenitiesList.add("Air Conditioning")
        if (currentState.securityChecked) amenitiesList.add("Security")
        updateField(AddPropertyField.Amenities, amenitiesList)
    }

    // Upload property
    fun saveProperty() {
        if (userId.isNullOrEmpty()) return

        val state = _uiState.value
        val property = Property(
            title = state.title,
            description = state.description,
            price = state.price,
            latitude = state.propertyLocation?.latitude,
            longitude = state.propertyLocation?.longitude,
            contactPhone = state.contactPhone,
            contactEmail = state.contactEmail,
            county = state.county,
            propertyType = state.propertyType,
            bedrooms = state.bedrooms,
            bathrooms = state.bathrooms,
            areaSize = state.areaSize,
            amenities = state.amenities,
            depositAmount = state.depositAmount,
            availableFrom = state.availableFrom,
            leaseTerms = state.leaseTerms,
            features = state.additionalFeatures,
            imageUrl = emptyList(), // AUTOGENERATED by FireBaseStorage
            videoUrl = emptyList(), // AUTOGENERATED by FireBaseStorage
            ownerId = userId,
            ownerName = null, //RESERVED, Not implemented yet
            active = true,  //RESERVED, Not implemented yet
            available = true, //RESERVED, Not implemented yet
            address = null,     //RESERVED, Not implemented yet
            createdAt = Date(),
            viewsCount = null,   //RESERVED, Not implemented yet
            id = null,          //AUTOGENERATED by FireBaseFireStore
            video = state.selectedVideoUris.isNotEmpty()
        )

        viewModelScope.launch {
            propertyRepository.uploadProperty(
                property,
                state.selectedImageUris,
                state.selectedVideoUris
            )
        }
    }

    // Clear all data
    fun clearData() {
        _uiState.value = AddPropertyUiState(
            countyNames = _uiState.value.countyNames,
            propertyTypes = _uiState.value.propertyTypes
        )
        // Reset individual fields
        _title.value = null
        _description.value = null
        _price.value = null
        _contactEmail.value = null
        _contactPhone.value = null
        _leaseTerms.value = null
        _availableFrom.value = null
        _bedrooms.value = null
        _bathrooms.value = null
        _areaSize.value = null
        _amenities.value = emptyList()
        _additionalFeatures.value = null
        _propertyLocation.value = null
        _wifiChecked.value = false
        _poolChecked.value = false
        _gymChecked.value = false
        _parkingChecked.value = false
        _airConditioningChecked.value = false
        _securityChecked.value = false
        _depositAmount.value = null
        _county.value = null
        _propertyType.value = null
    }
}

enum class AddPropertyField {
    Title, Description, Price, DepositAmount, AvailableFrom, LeaseTerms, AdditionalFeatures,
    SelectedImageUris, SelectedVideoUris, PropertyLocation, Amenities, PropertyType, AreaSize,
    ContactEmail, ContactPhone, Bedrooms, Bathrooms, County, WifiChecked, PoolChecked,
    GymChecked, ParkingChecked, AirConditioningChecked, SecurityChecked
}