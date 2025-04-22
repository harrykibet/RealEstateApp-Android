package com.application.real_estate_app.core_data.repositories

import com.application.real_estate_app.core_data.interfaces.IUserRepository
import com.application.real_estate_app.core_model.user.User
import com.application.real_estate_app.core_network.interfaces.IUserRemoteDataSource
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDataSource: IUserRemoteDataSource,
) : IUserRepository {
    override suspend fun getUserById(userId: String): User? {
        return remoteDataSource.getUserById(userId)
    }
}