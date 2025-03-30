package com.application.real_estate_app.core_common.interfaces

import com.application.real_estate_app.core_model.user.UserLocation

interface ILocationUtils {
    fun getLocationInfo(): UserLocation
}