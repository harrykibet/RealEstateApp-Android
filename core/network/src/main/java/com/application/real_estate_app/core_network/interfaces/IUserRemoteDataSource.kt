package com.application.real_estate_app.core_network.interfaces

import com.application.real_estate_app.core_model.User

interface IUserRemoteDataSource {
    suspend fun getUserInfo(userId : String) : User
}