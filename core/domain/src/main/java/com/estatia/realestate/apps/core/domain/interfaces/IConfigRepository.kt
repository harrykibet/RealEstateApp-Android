package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import kotlinx.coroutines.flow.StateFlow

interface IConfigRepository {

    val isInitialized: Boolean

    suspend fun initialize()

    suspend fun refresh()

    val baseUrl: String

    val isLoggingEnabled: Boolean

    val googleKeyPattern: Regex

    val genericKeyPattern: Regex

    val paymentsKeyPattern: Regex

    val cdnEndpoints: List<CdnEndpoint>

    val encryptionLocationId: String

    val encryptionKeyRingId: String

    val symmetricKeyId: String

    val asymmetricKeyId: String

    val asymmetricSigningKeyId: String

    val configVersion: StateFlow<Long>
}
