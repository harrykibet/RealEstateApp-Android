package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import javax.inject.Inject


/**
 * Global registry for translating low-level infrastructure failures into domain-specific business exceptions.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Orchestrate specialized mappers for different domain entities (Property, User, etc.).
 * - Concurrency: Stateless and thread-safe.
 * - Resilience: Enforces 100% coverage of infrastructure-to-domain error mapping.
 */
internal class ExceptionTranslator @Inject constructor(
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
