package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.exceptions.FirestoreException

interface IFirestoreErrorMapper {

    fun map(
        throwable: Throwable
    ): FirestoreException
}