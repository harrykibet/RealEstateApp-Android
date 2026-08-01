package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.common.exceptions.StorageException
import com.estatia.realestate.apps.core.common.exceptions.UserException
import com.estatia.realestate.apps.core.domain.interfaces.DataExceptionMapper
import javax.inject.Inject

class UserExceptionMapper @Inject constructor()
    : DataExceptionMapper<UserException> {


    override fun map(
        exception: InfrastructureException
    ): UserException {


        return when(exception){


            is DatabaseException ->
                mapDatabase(exception)


            is StorageException ->
                mapStorage(exception)
        }
    }



    private fun mapDatabase(
        exception: DatabaseException
    ): UserException {


        return when(exception){

            DatabaseException.NotFound ->
                UserException.UserNotFound


            DatabaseException.PermissionDenied ->
                UserException.PermissionDenied


            DatabaseException.AlreadyExists ->
                UserException.AlreadyExists


            else ->
                UserException.Unknown(exception)
        }
    }



    private fun mapStorage(
        exception: StorageException
    ): UserException {


        return when(exception){


            StorageException.UploadFailed ->
                UserException.UserCreationFailed


            StorageException.PermissionDenied ->
                UserException.PermissionDenied


            StorageException.ObjectNotFound ->
                UserException.UserNotFound


            else ->
                UserException.Unknown(exception)
        }
    }
}
