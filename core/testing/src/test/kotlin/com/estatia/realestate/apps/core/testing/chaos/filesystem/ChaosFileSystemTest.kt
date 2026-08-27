package com.estatia.realestate.apps.core.testing.chaos.filesystem

import com.estatia.realestate.apps.core.testing.fake.filesystem.FakeFileSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.io.IOException

/**
 * Concrete implementation of [ChaosFileSystemContract].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChaosFileSystemTest : ChaosFileSystemContract() {

    @Test
    fun `Success behavior works normally`() = runTest {
        val delegate = FakeFileSystem()
        val fs = ChaosFileSystem(delegate)
        val file = File("test.txt")
        val data = byteArrayOf(1, 2, 3)
        
        fs.writeBytes(file, data)
        assertTrue(fs.exists(file))
        assertTrue(fs.readBytes(file).contentEquals(data))
    }

    @Test
    fun `failNext only affects one operation`() = runTest {
        val fs = ChaosFileSystem(FakeFileSystem())
        fs.failNext(FileSystemBehavior.IoFailure)
        
        var exceptionThrown = false
        try {
            fs.exists(File("test.txt"))
        } catch (e: IOException) {
            exceptionThrown = true
        }
        
        assertTrue("Should have thrown IOException on first call", exceptionThrown)
        
        // Second call should succeed
        fs.exists(File("test.txt"))
    }
}
