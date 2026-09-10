package com.estatia.realestate.apps.core.ksp_architecture

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class Law018_ViewModelSsotProcessorTest {

    @Test
    fun `LAW-018 ViewModel multiple public StateFlows fails compilation`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import com.estatia.realestate.apps.core.architecture.annotations.ViewModelMarker
            import kotlinx.coroutines.flow.StateFlow
            
            @ViewModelMarker
            class TestViewModel {
                val state1: StateFlow<Int> = TODO()
                val state2: StateFlow<String> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(Law018_ViewModelSsotProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("LAW-018"))
    }

    @Test
    fun `LAW-018 ViewModel with single StateFlow and multiple event Flows passes`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import com.estatia.realestate.apps.core.architecture.annotations.ViewModelMarker
            import kotlinx.coroutines.flow.StateFlow
            import kotlinx.coroutines.flow.Flow
            
            @ViewModelMarker
            class TestViewModel {
                val uiState: StateFlow<Int> = TODO()
                val navigationEvents: Flow<String> = TODO()
                val effects: Flow<Unit> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(Law018_ViewModelSsotProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `LAW-018 ViewModel with no StateFlow fails`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import com.estatia.realestate.apps.core.architecture.annotations.ViewModelMarker
            import kotlinx.coroutines.flow.Flow
            
            @ViewModelMarker
            class TestViewModel {
                val events: Flow<String> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(Law018_ViewModelSsotProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("has no public StateFlow"))
    }

    @Test
    fun `LAW-018 class inheriting from ViewModel with no StateFlow fails without marker`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import androidx.lifecycle.ViewModel
            
            class TestViewModel : ViewModel() {
                // No StateFlow
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            KspTestUtils.lifecycleStubs,
            source,
            providers = listOf(Law018_ViewModelSsotProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("has no public StateFlow"))
    }

    @Test
    fun `LAW-018 ViewModel inheriting StateFlow and adding another fails`() {
        val baseSource = SourceFile.kotlin(
            "BaseViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import kotlinx.coroutines.flow.StateFlow
            import androidx.lifecycle.ViewModel
            
            abstract class BaseViewModel : ViewModel() {
                val baseState: StateFlow<Int> = TODO()
            }
            """.trimIndent()
        )
        
        val childSource = SourceFile.kotlin(
            "ChildViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import kotlinx.coroutines.flow.StateFlow
            
            class ChildViewModel : BaseViewModel() {
                val childState: StateFlow<String> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            KspTestUtils.lifecycleStubs,
            baseSource,
            childSource,
            providers = listOf(Law018_ViewModelSsotProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Multiple state authorities detected in 'ChildViewModel'"))
    }

    @Test
    fun `LAW-018 ViewModel with custom StateFlow subtype passes`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import kotlinx.coroutines.flow.StateFlow
            import androidx.lifecycle.ViewModel
            
            interface MyUiState<T> : StateFlow<T>
            
            class TestViewModel : ViewModel() {
                val state: MyUiState<Int> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            KspTestUtils.lifecycleStubs,
            source,
            providers = listOf(Law018_ViewModelSsotProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}
