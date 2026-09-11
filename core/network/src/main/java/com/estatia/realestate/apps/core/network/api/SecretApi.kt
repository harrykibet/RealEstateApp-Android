package com.estatia.realestate.apps.core.network.api

import retrofit2.http.GET
import retrofit2.http.Query
import com.estatia.realestate.apps.core.architecture.annotations.DataSource

/**
 * Retrofit interface for fetching secrets from the secure backend.
 */
@DataSource
interface SecretApi {

    /**
     * Fetches a secret value by its ID.
     * The call is expected to be protected by Firebase App Check.
     */
    @GET("getSecret")
    suspend fun getSecret(@Query("secretId") id: String): String
}
