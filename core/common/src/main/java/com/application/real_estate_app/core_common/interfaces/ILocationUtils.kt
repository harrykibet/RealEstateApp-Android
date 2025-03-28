package com.application.real_estate_app.core_common.interfaces

import com.application.real_estate_app.core_model.UserLocation

interface ILocationUtils {
    fun getLocationInfo(): UserLocation
}