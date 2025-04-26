package com.application.real_estate_app.core.domain.interfaces

import com.application.real_estate_app.core.domain.models.UserLocation

interface ILocationUtils {
    fun getLocationInfo(): UserLocation
}