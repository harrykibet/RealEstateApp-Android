package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.StorageException

interface IFirebaseStorageErrorMapper {

    fun map(
        throwable: Throwable
    ): StorageException
}