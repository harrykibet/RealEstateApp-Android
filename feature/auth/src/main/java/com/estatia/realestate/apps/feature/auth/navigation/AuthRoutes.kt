package com.estatia.realestate.apps.feature.auth.navigation

object AuthRoutes {
    const val GRAPH = "auth_graph"

    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val FORGOT_PASSWORD = "forgot_password"
    const val EMAIL_VERIFICATION = "email_verification"
    const val PHONE_VERIFICATION =
        "phone_verification/{verificationId}/{phoneNumber}"
}
