package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Serializable
@Helper
data object AuthBaseRoute

@Serializable
@Helper
data object LoginRoute

@Serializable
@Helper
data object SignUpRoute

@Serializable
@Helper
data object ForgotPasswordRoute

@Serializable
@Helper
data object EmailVerificationRoute

@Serializable
@Helper
data object PhoneVerificationRoute
