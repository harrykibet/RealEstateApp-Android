package com.application.real_estate_app.core_network.interfaces

import com.application.real_estate_app.core_model.user.User

interface IUserRemoteDataSource {
    suspend fun getUserById(userId: String): User?
}