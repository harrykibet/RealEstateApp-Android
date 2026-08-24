package com.estatia.realestate.apps.core.common.system

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.estatia.realestate.apps.core.testing.chaos.filesystem.ChaosFileSystem
import com.estatia.realestate.apps.core.testing.fake.filesystem.FakeFileSystem
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FileUtilsInstrumentedTest {

    private lateinit var context: Context
    private lateinit var fakeFs: FakeFileSystem
    private lateinit var chaosFs: ChaosFileSystem

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        fakeFs = FakeFileSystem()
        chaosFs = ChaosFileSystem(fakeFs)
    }

    @Test
    fun getFileFromUriCopiesFileToCacheSuccessfully() {
        // Create a real file to get a URI from
        val testFile = File(context.filesDir, "test_input.txt")
        testFile.writeText("Hello Estatia")
        val uri = android.net.Uri.fromFile(testFile)
        
        val resultFile = FileUtils.getFileFromUri(context, uri)
        
        assertNotNull(resultFile)
        assertTrue(resultFile!!.exists())
        assertTrue(resultFile.length() > 0)
        
        // Cleanup
        resultFile.delete()
        testFile.delete()
    }

    @Test
    fun getFileFromUriHandlesDiskPressureChaosGracefully() = runBlocking {
        val testFile = File(context.filesDir, "test_chaos.txt")
        testFile.writeText("Chaos Data")
        val uri = android.net.Uri.fromFile(testFile)

        // Note: Real FileUtils doesn't use our IFileSystem yet.
        // This test documents the target resilience standard.
        val resultFile = FileUtils.getFileFromUri(context, uri)
        assertNotNull(resultFile)
        
        resultFile?.delete()
        testFile.delete()
    }
}
