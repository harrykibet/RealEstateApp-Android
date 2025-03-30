package com.application.real_estate_app.core_data.interfaces

import com.application.real_estate_app.core_model.user.User

interface IUserRepository {
    suspend fun getUserById(userId: String): User?
}