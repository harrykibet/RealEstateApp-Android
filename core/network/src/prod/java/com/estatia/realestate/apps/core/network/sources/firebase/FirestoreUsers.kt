package com.estatia.realestate.apps.core.network.sources.firebase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


/**
 * Firebase implementation of [IUserRemoteDataSource].
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage user profile persistence in Firestore.
 * - Concurrency: Thread-safe; delegates context to [networkClient].
 * - Resilience: Surfaces [DatabaseException.NotFound] if the profile doesn't exist.
 * - Observability: Tracks profile fetch latency and cache performance.
 */
internal class FirestoreUsers @Inject constructor(
    private val database: FirebaseFirestore,
    private val networkClient: INetworkClient,
    private val metricsTracker: IMetricsTracker
) : IUserRemoteDataSource {


    override suspend fun getUserById(
        userId:String
    ): AppResult<UserEntityModel> {


        require(userId.isNotBlank()) {
            "User id cannot be empty"
        }

        val startTime = System.currentTimeMillis()

        return networkClient.execute {


            val snapshot =
                database.collection(
                    FirestoreCollections.USERS
                )
                    .document(userId)
                    .get()
                    .await()



            val user = snapshot.toObject(
                UserEntityModel::class.java
            ) ?: throw DatabaseException.NotFound

            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("network.users.fetch_latency", duration.milliseconds)
            metricsTracker.incrementCounter("network.users.fetch_success")

            user
        }
    }
}
