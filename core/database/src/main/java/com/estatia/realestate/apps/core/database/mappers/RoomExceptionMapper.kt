package com.estatia.realestate.apps.core.database.mappers

import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.database.interfaces.IRoomExceptionMapper
import javax.inject.Inject

internal class RoomExceptionMapper @Inject constructor() : IRoomExceptionMapper {

    override fun map(
        throwable: Throwable
    ): DatabaseException {

        return when (throwable) {

            is SQLiteConstraintException ->
                DatabaseException.ConstraintViolation(throwable)

            is SQLiteDatabaseCorruptException ->
                DatabaseException.CorruptedDatabase(throwable)

            is SQLiteDiskIOException ->
                DatabaseException.DiskIO(throwable)

            is SQLiteFullException ->
                DatabaseException.StorageFull(throwable)

            is SQLException ->
                DatabaseException.QueryFailed(throwable)

            else ->
                DatabaseException.Unknown(throwable)
        }
    }
}
