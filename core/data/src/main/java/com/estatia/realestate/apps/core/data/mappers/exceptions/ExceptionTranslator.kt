package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.data.interfaces.IExceptionTranslator
import javax.inject.Inject


class ExceptionTranslator @Inject constructor(
    private val propertyMapper: PropertyExceptionMapper,
    private val userMapper: UserExceptionMapper,
    private val commentMapper: CommentExceptionMapper,
    private val searchMapper: SearchExceptionMapper
) : IExceptionTranslator {


    override fun translateProperty(
        exception: InfrastructureException
    ): AppException {

        return propertyMapper.map(exception)
    }


    override fun translateUser(
        exception: InfrastructureException
    ): AppException {

        return userMapper.map(exception)
    }


    override fun translateComment(
        exception: InfrastructureException
    ): AppException {

        return commentMapper.map(exception)
    }


    override fun translateSearch(
        exception: InfrastructureException
    ): AppException {

        return searchMapper.map(exception)
    }
}