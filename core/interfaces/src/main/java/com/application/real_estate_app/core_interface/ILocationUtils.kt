package com.application.real_estate_app.core_interface

import com.application.real_estate_app.core_model.UserLocation

interface ILocationUtils {
    fun getLocationInfo(): UserLocation
}