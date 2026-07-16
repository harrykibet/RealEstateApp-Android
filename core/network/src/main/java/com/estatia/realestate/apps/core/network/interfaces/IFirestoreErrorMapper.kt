package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.DatabaseException

interface IFirestoreErrorMapper {

    fun map(
        throwable: Throwable
    ): DatabaseException
}