package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.common.exceptions.AppException

interface DataExceptionMapper<T : AppException> {

    fun map(
        exception: InfrastructureException
    ): T
}
