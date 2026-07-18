package com.estatia.realestate.apps.core.data.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.DomainMappableException

interface IExceptionTranslator {

    fun translateProperty(
        exception: DomainMappableException
    ): AppException


    fun translateUser(
        exception: DomainMappableException
    ): AppException


    fun translateComment(
        exception: DomainMappableException
    ): AppException


    fun translateSearch(
        exception: DomainMappableException
    ): AppException
}