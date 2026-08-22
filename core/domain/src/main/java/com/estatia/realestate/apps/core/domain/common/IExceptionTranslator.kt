package com.estatia.realestate.apps.core.domain.common

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException

interface IExceptionTranslator {

    fun translateProperty(
        exception: InfrastructureException
    ): AppException


    fun translateUser(
        exception: InfrastructureException
    ): AppException


    fun translateComment(
        exception: InfrastructureException
    ): AppException


    fun translateSearch(
        exception: InfrastructureException
    ): AppException
}
