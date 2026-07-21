package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.common.exceptions.DatabaseException

interface IRoomExceptionMapper {

    fun map(throwable: Throwable): DatabaseException
}