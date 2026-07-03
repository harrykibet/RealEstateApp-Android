package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.db_entities.UserEntity

interface IUserRemoteDataSource {
    suspend fun getUserById(userId: String): UserEntity?
}