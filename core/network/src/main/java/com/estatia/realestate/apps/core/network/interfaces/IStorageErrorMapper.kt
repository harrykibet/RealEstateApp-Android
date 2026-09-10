package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.StorageException
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface IStorageErrorMapper {
    fun map(throwable: Throwable): StorageException
}
