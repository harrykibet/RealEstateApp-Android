package com.application.real_estate_app.feature_auth.data.services


import com.application.real_estate_app.core.domain.interfaces.AuthApiInterface
import com.application.real_estate_app.feature_auth.domain.interfaces.IAuthApi
import javax.inject.Inject

class ImplAuthApiCore @Inject constructor(
    private val api: IAuthApi
) : AuthApiInterface {

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
