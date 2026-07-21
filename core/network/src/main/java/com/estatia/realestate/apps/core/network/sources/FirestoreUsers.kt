package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirestoreUsers @Inject constructor(
    private val database: FirebaseFirestore,
    private val networkClient: INetworkClient
) : IUserRemoteDataSource {


    override suspend fun getUserById(
        userId:String
    ): AppResult<UserEntityModel> {


        require(userId.isNotBlank()) {
            "User id cannot be empty"
        }


        return networkClient.execute {


            val snapshot =
                database.collection(
                    FirestoreCollections.USERS
                )
                    .document(userId)
                    .get()
                    .await()



            snapshot.toObject(
                UserEntityModel::class.java
            )
                ?: throw DatabaseException.NotFound
        }
    }
}