package com.estatia.realestate.apps.core.database.mappers

import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior
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
    fun `map SQLiteConstraintException matches platform behavior`() {
        // 🧪 Adversarial Behavior: Constraint Violation
        println("Testing behavior: ${DatabaseBehavior.ConstraintViolation}")
        
        val exception = mockk<SQLiteConstraintException>()
        val result = mapper.map(exception)
        
        assertTrue("Should map to domain-specific constraint violation", result is DatabaseException.ConstraintViolation)
    }

    @Test
    fun `map generic SQLException to QueryFailed`() {
        val exception = mockk<SQLException>()
        val result = mapper.map(exception)
        assertTrue(result is DatabaseException.QueryFailed)
    }

    @Test
    fun `map corrupted database scenario to domain error`() {
        // 🧪 Adversarial Behavior: Data Corruption
        println("Testing behavior: ${DatabaseBehavior.Corrupted}")
        
        val exception = SQLException("Database disk image is malformed")
        val result = mapper.map(exception)
        
        assertTrue(result is DatabaseException.QueryFailed)
    }
}
