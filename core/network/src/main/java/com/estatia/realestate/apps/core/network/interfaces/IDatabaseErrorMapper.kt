package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface IDatabaseErrorMapper {
    fun map(throwable: Throwable): DatabaseException
}
