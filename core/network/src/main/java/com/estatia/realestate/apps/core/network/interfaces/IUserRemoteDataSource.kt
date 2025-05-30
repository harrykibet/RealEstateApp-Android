package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.model.user.User

interface IUserRemoteDataSource {
    suspend fun getUserById(userId: String): User?
}