package com.application.real_estate_app.feature_property.data

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class AddPropertyUiState(
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val depositAmount: Double = 0.0,
    val availableFrom: String? = null,
    val leaseTerms: String = "",
    val additionalFeatures: String = "",
    val selectedImageUris: List<Uri> = emptyList(),
    val selectedVideoUris: List<Uri> = emptyList(),
    val propertyLocation: LatLng? = null,
    val amenities: List<String> = emptyList(),
    val propertyType: String = "",
    val areaSize: Double = 0.0,
    val contactEmail: String = "",
    val contactPhone: String = "",
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val county: String = "",
    val wifiChecked: Boolean = false,
    val poolChecked: Boolean = false,
    val gymChecked: Boolean = false,
    val parkingChecked: Boolean = false,
    val airConditioningChecked: Boolean = false,
    val securityChecked: Boolean = false,
    val countyNames: List<String> = emptyList(),
    val propertyTypes: List<String> = emptyList()
)
