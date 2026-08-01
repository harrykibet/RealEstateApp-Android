package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.CommentException
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.domain.interfaces.DataExceptionMapper
import javax.inject.Inject

class CommentExceptionMapper @Inject constructor()
    : DataExceptionMapper<CommentException> {



    override fun map(
        exception: InfrastructureException
    ): CommentException {


        return when(exception){


            is DatabaseException ->
                mapDatabase(exception)

            else ->
                CommentException.Unknown(exception as Throwable)
        }
    }



    private fun mapDatabase(
        exception:DatabaseException
    ):CommentException {


        return when(exception){


            DatabaseException.NotFound ->
                CommentException.CommentNotFound


            DatabaseException.PermissionDenied ->
                CommentException.PermissionDenied


            else ->
                CommentException.Unknown(exception)
        }
    }
}
