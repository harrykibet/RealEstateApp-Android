package com.application.real_estate_app.core.domain.interfaces

interface IRemoteConfigManager {
    fun getGoogleKeyPattern(): String
    fun getGenericKeyPattern(): String
    fun getPaymentsKeyPattern(): String
    fun getKeyRingLocationId(): String
    fun getKeyRingId(): String
    fun getSymmetricKeyId(): String
    fun getAsymmetricKeyId(): String
    fun getAsymmetricSigningKeyId(): String
}