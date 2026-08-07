package com.estatia.realestate.apps.core.data.util

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.mapError
import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.domain.interfaces.IExceptionTranslator


internal fun <T> AppResult<T>.translateUserFailures(
    translator: IExceptionTranslator
): AppResult<T> {

    return mapError { exception ->

        when(exception){

            is InfrastructureException ->
                translator.translateUser(exception)

            else ->
                exception
        }
    }
}
