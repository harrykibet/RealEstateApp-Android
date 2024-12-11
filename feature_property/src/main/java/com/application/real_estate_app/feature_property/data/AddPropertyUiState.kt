package com.application.real_estate_app.feature_property.data

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class AddPropertyUiState(
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val depositAmount: Double? = null,
    val availableFrom: String? = null,
    val leaseTerms: String? = null,
    val additionalFeatures: String? = null,
    val selectedImageUris: MutableList<Uri> = mutableListOf(),
    val selectedVideoUris: MutableList<Uri> = mutableListOf(),
    val propertyLocation: LatLng? = null,
    val amenities: List<String> = emptyList(),
    val propertyType: String? = null,
    val areaSize: Double? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val county: String? = null,
    val wifiChecked: Boolean = false,
    val poolChecked: Boolean = false,
    val gymChecked: Boolean = false,
    val parkingChecked: Boolean = false,
    val airConditioningChecked: Boolean = false,
    val securityChecked: Boolean = false,
    val countyNames: List<String> = emptyList(),
    val propertyTypes: List<String> = emptyList()
)
