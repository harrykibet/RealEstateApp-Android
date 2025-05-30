package com.estatia.realestate.apps.core.common.interfaces

import com.estatia.realestate.apps.core.model.user.UserLocation

interface ILocationUtils {
    fun getLocationInfo(): UserLocation
}