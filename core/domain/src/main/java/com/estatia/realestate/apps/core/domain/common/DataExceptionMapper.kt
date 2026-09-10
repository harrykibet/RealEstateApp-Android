package com.estatia.realestate.apps.core.domain.common

import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface DataExceptionMapper<T : AppException> {

    fun map(
        exception: InfrastructureException
    ): T
}
