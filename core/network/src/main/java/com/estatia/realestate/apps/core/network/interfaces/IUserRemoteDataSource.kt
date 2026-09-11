package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.architecture.annotations.DataSource


@DataSource
interface IUserRemoteDataSource {

    suspend fun getUserById(
        userId: String
    ): AppResult<UserEntityModel>
}
