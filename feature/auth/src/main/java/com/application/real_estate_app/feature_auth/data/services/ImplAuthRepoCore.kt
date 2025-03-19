package com.application.real_estate_app.feature_auth.data.services

import com.application.real_estate_app.core_interface.AuthRepoInterface
import com.application.real_estate_app.feature_auth.domain.interfaces.IAuthRepo
import javax.inject.Inject

class ImplAuthRepoCore @Inject constructor(
    private val api: IAuthRepo
) : AuthRepoInterface {

    override fun isUserAuthenticated(): Boolean {
        return api.isUserAuthenticated()
    }

    override fun getCurrentUserId(): String? {
        return api.getCurrentUserId()
    }

    override fun getCurrentUserEmail(): String? {
        return api.getCurrentUserEmail()
    }

    override fun signOut(onFailure: (Exception) -> Unit) {
        api.signOut(onFailure)
    }
}
