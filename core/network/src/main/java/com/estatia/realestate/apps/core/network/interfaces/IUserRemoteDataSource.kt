package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.common.errors.AppResult


interface IUserRemoteDataSource {

    suspend fun getUserById(
        userId: String
    ): AppResult<UserEntityModel>
}