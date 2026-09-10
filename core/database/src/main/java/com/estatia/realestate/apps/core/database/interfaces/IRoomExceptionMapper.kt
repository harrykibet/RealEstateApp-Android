package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface IRoomExceptionMapper {

    fun map(throwable: Throwable): DatabaseException
}
