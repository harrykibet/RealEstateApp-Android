package com.estatia.realestate.apps.core.common.concurrency

import com.estatia.realestate.apps.core.architecture.annotations.Helper
import android.os.Looper

/**
 * Utility for enforcing thread confinement across architectural layers.
 */
@Helper
object Confinement {
    /**
     * Throws an [IllegalStateException] if called from any thread other than the Main thread.
     */
    fun checkMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException(
                "Thread Confinement Violation: This component must only be accessed from the Main thread. " +
                "Current thread: ${Thread.currentThread().name}"
            )
        }
    }
}
