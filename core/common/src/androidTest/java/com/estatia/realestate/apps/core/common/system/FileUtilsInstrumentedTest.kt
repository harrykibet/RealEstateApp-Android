package com.estatia.realestate.apps.core.common.system

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FileUtilsInstrumentedTest {

    @Test
    fun getFileFromUri_copiesFileToCache() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
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
}
