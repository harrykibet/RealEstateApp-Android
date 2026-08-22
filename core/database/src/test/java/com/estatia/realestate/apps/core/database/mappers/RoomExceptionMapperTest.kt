package com.estatia.realestate.apps.core.database.mappers

import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RoomExceptionMapperTest {

    private lateinit var mapper: RoomExceptionMapper

    @Before
    fun setup() {
        mapper = RoomExceptionMapper()
    }

    @Test
    fun `map SQLiteConstraintException to ConstraintViolation`() {
        val exception = mockk<SQLiteConstraintException>()
        val result = mapper.map(exception)
        assertTrue(result is DatabaseException.ConstraintViolation)
    }

    @Test
    fun `map generic SQLException to QueryFailed`() {
        val exception = mockk<SQLException>()
        val result = mapper.map(exception)
        assertTrue(result is DatabaseException.QueryFailed)
    }

    @Test
    fun `map unknown exception to Unknown`() {
        val exception = Exception("Random error")
        val result = mapper.map(exception)
        assertTrue(result is DatabaseException.Unknown)
    }
}
