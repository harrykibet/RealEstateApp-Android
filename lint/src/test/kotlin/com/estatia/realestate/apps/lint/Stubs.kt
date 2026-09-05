package com.estatia.realestate.apps.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin

object Stubs {
    val COROUTINES = kotlin(
        """
        package kotlinx.coroutines
        
        interface CoroutineScope
        interface Job
        interface CoroutineDispatcher
        interface Deferred<out T> { suspend fun await(): T }
        
        object Dispatchers {
            @JvmField val IO: CoroutineDispatcher = object : CoroutineDispatcher {}
            @JvmField val Main: CoroutineDispatcher = object : CoroutineDispatcher {}
            @JvmField val Default: CoroutineDispatcher = object : CoroutineDispatcher {}
            @JvmField val Unconfined: CoroutineDispatcher = object : CoroutineDispatcher {}
        }
        
        object GlobalScope : CoroutineScope
        
        fun CoroutineScope(context: Any): CoroutineScope = TODO()
        fun MainScope(): CoroutineScope = TODO()
        
        val CoroutineScope.isActive: Boolean
            get() = true
        
        fun CoroutineScope.launch(
            context: Any? = null,
            start: Any? = null,
            block: suspend CoroutineScope.() -> Unit
        ): Job = TODO()
        
        fun <T> CoroutineScope.async(
            context: Any? = null,
            start: Any? = null,
            block: suspend CoroutineScope.() -> T
        ): Deferred<T> = TODO()
        
        suspend fun yield(): Unit = TODO()
        fun CoroutineScope.ensureActive(): Unit = TODO()
        fun Job.ensureActive(): Unit = TODO()
        
        suspend fun <T> withContext(
            context: CoroutineDispatcher,
            block: suspend CoroutineScope.() -> T
        ): T = TODO()
        """.trimIndent()
    )

    val FLOW = kotlin(
        """
        package kotlinx.coroutines.flow
        
        interface Flow<out T>
        interface MutableStateFlow<T> : Flow<T>
        interface MutableSharedFlow<T> : Flow<T>
        
        fun <T> flow(block: suspend () -> T): Flow<T> = TODO()
        fun <T> flowOf(vararg elements: T): Flow<T> = TODO()
        fun <T> Flow<T>.buffer(capacity: Int = -1): Flow<T> = TODO()
        """.trimIndent()
    )

    val DAGGER = kotlin(
        """
        package dagger
        annotation class Module
        annotation class Provides
        """.trimIndent()
    )

    val RESULT = kotlin(
        """
        package kotlin
        class Result<out T>
        annotation class Suppress(vararg val names: String)
        """.trimIndent()
    )

    val COMPOSE = kotlin(
        """
        package androidx.compose.runtime
        
        annotation class Composable
        
        fun <T> remember(block: () -> T): T = TODO()
        
        interface MutableState<T> {
            var value: T
        }
        
        fun <T> mutableStateOf(value: T): MutableState<T> = TODO()
        """.trimIndent()
    )

    val COMPOSE_UI = kotlin(
        """
        package androidx.compose.ui
        
        import androidx.compose.runtime.Composable
        
        @Composable
        fun Text(text: String) {}
        """.trimIndent()
    )

    val VIEWMODEL = kotlin(
        """
        package androidx.lifecycle
        
        abstract class ViewModel
        """.trimIndent()
    )

    val DAGGER_HILT = kotlin(
        """
        package javax.inject
        annotation class Singleton
        annotation class Inject
        """.trimIndent()
    )

    val ANDROID_APP = kotlin(
        """
        package android.app
        abstract class Activity
        abstract class Service
        """.trimIndent()
    )

    val ANDROID_ANNOTATION = kotlin(
        """
        package android.annotation
        annotation class SuppressLint(vararg val value: String)
        """.trimIndent()
    )

    val ANDROID_CONTENT = kotlin(
        """
        package android.content
        abstract class Context
        """.trimIndent()
    )

    val ANDROID_VIEW = kotlin(
        """
        package android.view
        abstract class View
        """.trimIndent()
    )

    val COLLECTIONS = kotlin(
        """
        package kotlin.collections
        fun <T> emptyList(): List<T> = TODO()
        interface List<out T>
        """.trimIndent()
    )

    val MOCKK = kotlin(
        """
        package io.mockk
        fun <T> mockk(): T = TODO()
        """.trimIndent()
    )

    val TIMBER = kotlin(
        """
        package timber.log
        object Timber {
            fun d(message: String, vararg args: Any?) {}
            fun e(t: Throwable, message: String, vararg args: Any?) {}
        }
        """.trimIndent()
    )

    val ANDROID_LOG = kotlin(
        """
        package android.util
        object Log {
            fun d(tag: String, msg: String): Int = 0
            fun e(tag: String, msg: String): Int = 0
        }
        """.trimIndent()
    )

    val COMPOSE_UI_UNIT = kotlin(
        """
        package androidx.compose.ui.unit
        val Int.dp: Dp
            get() = Dp(this.toFloat())
        val Int.sp: Sp
            get() = Sp(this.toFloat())
        fun dp(value: Int): Dp = Dp(value.toFloat())
        fun sp(value: Int): Sp = Sp(value.toFloat())
        fun Int.dp(): Dp = Dp(this.toFloat())
        fun Int.sp(): Sp = Sp(this.toFloat())
        inline class Dp(val value: Float)
        inline class Sp(val value: Float)
        """.trimIndent()
    )

    val COMPOSE_UI_GRAPHICS = kotlin(
        """
        package androidx.compose.ui.graphics
        class Color(val value: Long) {
            companion object {
                val Red = Color(0xFFFF0000)
                val Blue = Color(0xFF0000FF)
            }
        }
        """.trimIndent()
    )
}
