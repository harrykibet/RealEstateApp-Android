package com.application.real_estate_app.domain.interfaces

import com.application.real_estate_app.domain.models.User

interface IUserRepository {
    fun createUserIfNotExists(userId: String?, user: User)
}