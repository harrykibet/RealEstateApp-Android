package com.estatia.realestate.apps.core.navigation

import kotlinx.serialization.Serializable

/**
 * Shared navigation route definitions for the Estatia app.
 * Using a centralized module prevents direct dependencies between features.
 */

// --- Auth ---
@Serializable
data object AuthBaseRoute

@Serializable
data object LoginRoute

@Serializable
data object SignUpRoute

@Serializable
data object ForgotPasswordRoute

@Serializable
data object EmailVerificationRoute

@Serializable
data object PhoneVerificationRoute

// --- Home ---
@Serializable
data object HomeRoute

@Serializable
data object HomeBaseRoute

// --- Search ---
@Serializable
data object SearchRoute

@Serializable
data object SearchBaseRoute

// --- Property ---
@Serializable
data class PropertyDetailRoute(val propertyId: String)

@Serializable
data object PropertyRoute

@Serializable
data object PropertyMediaCaptureRoute

@Serializable
data object PropertyBaseRoute

// --- Favorites ---
@Serializable
data object FavoritesRoute

@Serializable
data object FavoritesBaseRoute

// --- Profile ---
@Serializable
data object ProfileRoute

@Serializable
data object ProfileBaseRoute

// --- Settings ---
@Serializable
data object SettingsRoute

@Serializable
data object SettingsBaseRoute

// --- Chats ---
@Serializable
data object ChatsRoute

@Serializable
data object ChatsBaseRoute

@Serializable
data class ChatDetailRoute(val chatId: String)

// --- Market ---
@Serializable
data object MarketRoute

@Serializable
data object MarketBaseRoute

// --- Comments ---
@Serializable
data object CommentsBaseRoute

@Serializable
data class CommentsRoute(val propertyId: String)
