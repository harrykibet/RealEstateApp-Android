package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.common.exceptions.AppResult

interface ISearchRemoteDataSource {


    suspend fun searchProperties(
        query:String,
        limit:Int
    ): AppResult<List<PropertyEntityModel>>


    suspend fun getNearbyProperties(
        latitude:Double,
        longitude:Double,
        radiusKm:Double
    ): AppResult<List<PropertyEntityModel>>
}
