package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.DomainMappableException
import com.estatia.realestate.apps.core.data.interfaces.IExceptionTranslator
import javax.inject.Inject


class ExceptionTranslator @Inject constructor(
    private val propertyMapper: PropertyExceptionMapper,
    private val userMapper: UserExceptionMapper,
    private val commentMapper: CommentExceptionMapper,
    private val searchMapper: SearchExceptionMapper
) : IExceptionTranslator {


    override fun translateProperty(
        exception: DomainMappableException
    ): AppException {

        return propertyMapper.map(exception)
    }


    override fun translateUser(
        exception: DomainMappableException
    ): AppException {

        return userMapper.map(exception)
    }


    override fun translateComment(
        exception: DomainMappableException
    ): AppException {

        return commentMapper.map(exception)
    }


    override fun translateSearch(
        exception: DomainMappableException
    ): AppException {

        return searchMapper.map(exception)
    }
}