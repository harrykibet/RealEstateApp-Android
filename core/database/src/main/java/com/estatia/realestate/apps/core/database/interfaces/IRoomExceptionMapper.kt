package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.architecture.annotations.DataSource

@DataSource
interface IRoomExceptionMapper {

    fun map(throwable: Throwable): DatabaseException
}
