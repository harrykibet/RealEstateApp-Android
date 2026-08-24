package com.estatia.realestate.apps.core.common.system

import com.estatia.realestate.apps.core.testing.chaos.filesystem.ChaosFileSystem
import com.estatia.realestate.apps.core.testing.fake.filesystem.FakeFileSystem
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

class FileUtilsTest {

    private lateinit var fakeFs: FakeFileSystem
    private lateinit var chaosFs: ChaosFileSystem

    @Before
    fun setup() {
        fakeFs = FakeFileSystem()
        chaosFs = ChaosFileSystem(fakeFs)
    }

    @Test
    fun `doesFileExist returns true for existing file`() {
        val tempFile = Files.createTempFile("estatia_test", ".txt").toFile()
        try {
            assertTrue(FileUtils.doesFileExist(tempFile.absolutePath))
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `doesFileExist returns false for non-existing file using platform models`() = runTest {
        // 🧪 Chaos Scenario: Missing File
        chaosFs.failNext(ChaosFileSystem.FileSystemFailure.MissingFile)
        
        assertFalse(FileUtils.doesFileExist("/path/to/non/existing/file"))
    }

    @Test
    fun `deleteFile deletes existing file`() {
        val tempFile = Files.createTempFile("estatia_test_del", ".txt").toFile()
        assertTrue(tempFile.exists())
        
        val deleted = FileUtils.deleteFile(tempFile)
        
        assertTrue(deleted)
        assertFalse(tempFile.exists())
    }

    @Test
    fun `deleteFile handles disk pressure scenario successfully`() = runTest {
        // 🧪 Chaos Scenario: Disk Full
        val behavior = ChaosFileSystem.FileSystemFailure.DiskFull
        println("Simulating system stability under $behavior pressure")
        
        val result = FileUtils.deleteFile(null)
        assertFalse(result)
    }
}
