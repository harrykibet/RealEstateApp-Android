package com.application.real_estate_app.feature_auth.data.services


import com.application.real_estate_app.core.interfaces.IAuthApiCore
import com.application.real_estate_app.feature_auth.domain.interfaces.IAuthApi
import javax.inject.Inject

class AuthApiCore @Inject constructor(
    private val api: IAuthApi
) : IAuthApiCore {

    override fun isUserAuthenticated(): Boolean {
        return api.isUserAuthenticated()
    }

    override fun getCurrentUserId(): String? {
        return api.getCurrentUserId()
    }

    override fun getCurrentUserEmail(): String? {
        return api.getCurrentUserEmail()
    }

    override fun signOut() {
        api.signOut()
    }
}
