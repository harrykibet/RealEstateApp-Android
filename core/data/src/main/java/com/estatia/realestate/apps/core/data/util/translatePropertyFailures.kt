package com.estatia.realestate.apps.core.data.util

import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.common.errors.mapError
import com.estatia.realestate.apps.core.common.exceptions.DomainMappableException
import com.estatia.realestate.apps.core.data.interfaces.IExceptionTranslator


fun <T> AppResult<T>.translatePropertyFailures(
    translator: IExceptionTranslator
): AppResult<T> {

    return mapError { exception ->

        when(exception){

            is DomainMappableException ->
                translator.translateProperty(exception)

            else ->
                exception
        }
    }
}