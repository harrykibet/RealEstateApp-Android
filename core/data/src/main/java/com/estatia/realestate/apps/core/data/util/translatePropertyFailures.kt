package com.estatia.realestate.apps.core.data.util

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.mapError
import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.data.interfaces.IExceptionTranslator


fun <T> AppResult<T>.translatePropertyFailures(
    translator: IExceptionTranslator
): AppResult<T> {

    return mapError { exception ->

        when(exception){

            is InfrastructureException ->
                translator.translateProperty(exception)

            else ->
                exception
        }
    }
}