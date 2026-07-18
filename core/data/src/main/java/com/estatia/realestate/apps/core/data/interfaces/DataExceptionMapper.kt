package com.estatia.realestate.apps.core.data.interfaces

import com.estatia.realestate.apps.core.common.exceptions.DomainMappableException
import com.estatia.realestate.apps.core.common.exceptions.AppException

interface DataExceptionMapper<T : AppException> {

    fun map(
        exception: DomainMappableException
    ): T
}