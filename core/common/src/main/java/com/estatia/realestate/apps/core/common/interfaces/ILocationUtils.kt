package com.estatia.realestate.apps.core.common.interfaces

import com.estatia.realestate.apps.core.model.user.UserLocation
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface ILocationUtils {
    suspend fun getLocationInfo(): UserLocation
}
