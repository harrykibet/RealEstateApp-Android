package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.common.exceptions.DomainMappableException
import com.estatia.realestate.apps.core.common.exceptions.PropertyException
import com.estatia.realestate.apps.core.common.exceptions.StorageException
import com.estatia.realestate.apps.core.data.interfaces.DataExceptionMapper
import javax.inject.Inject

class PropertyExceptionMapper @Inject constructor()
    : DataExceptionMapper<AppException> {


    override fun map(
        exception: DomainMappableException
    ): PropertyException {

        return when(exception) {


            is DatabaseException ->
                mapDatabaseException(exception)


            is StorageException ->
                mapStorageException(exception)
        }
    }



    private fun mapDatabaseException(
        exception: DatabaseException
    ): PropertyException {


        return when(exception) {

            DatabaseException.NotFound ->
                PropertyException.PropertyNotFound


            DatabaseException.PermissionDenied ->
                PropertyException.PermissionDenied


            else ->
                PropertyException.Unknown(exception)
        }
    }



    private fun mapStorageException(
        exception: StorageException
    ): PropertyException {


        return when(exception) {

            StorageException.UploadFailed ->
                PropertyException.PropertyCreationFailed


            StorageException.PermissionDenied ->
                PropertyException.PermissionDenied


            StorageException.ObjectNotFound ->
                PropertyException.PropertyNotFound


            else ->
                PropertyException.Unknown(exception)
        }
    }
}