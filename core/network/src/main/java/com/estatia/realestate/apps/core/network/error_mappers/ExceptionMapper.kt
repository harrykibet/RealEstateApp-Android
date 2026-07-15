package com.estatia.realestate.apps.core.network.error_mappers

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject

class ExceptionMapper @Inject constructor(
    private val networkMapper: NetworkErrorMapper,
    private val authMapper: FirebaseAuthErrorMapper,
    private val firestoreMapper: FirestoreErrorMapper
) : IExceptionMapper {


    override fun map(
        throwable: Throwable
    ): AppException {

        return when {

            throwable is FirebaseAuthException ->
                authMapper.map(throwable)


            throwable is FirebaseFirestoreException ->
                firestoreMapper.map(throwable)


            else ->
                networkMapper.map(throwable)
        }
    }
}