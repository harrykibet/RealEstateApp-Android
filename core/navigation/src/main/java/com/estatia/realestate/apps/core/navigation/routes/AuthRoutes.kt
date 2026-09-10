package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Serializable
@Helper
data object AuthBaseRoute

@Serializable
data object LoginRoute

@Serializable
@Helper
data object SignUpRoute

@Serializable
data object ForgotPasswordRoute

@Serializable
@Helper
data object EmailVerificationRoute

@Serializable
data object PhoneVerificationRoute
