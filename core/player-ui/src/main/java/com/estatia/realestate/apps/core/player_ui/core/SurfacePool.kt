package com.estatia.realestate.apps.core.player_ui.core

import android.content.Context
import android.view.SurfaceView
import android.view.ViewGroup
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayDeque

/**
 * A thread-safe pool for reusing [SurfaceView] instances.
 * Dynamically adjusts its capacity based on [IPlayerPoolSizingPolicy].
 */
@Singleton
class SurfacePool @Inject constructor(
    private val sizingPolicy: IPlayerPoolSizingPolicy
) {
    private val pool = ArrayDeque<SurfaceView>()

    fun acquire(context: Context): SurfaceView {
        return synchronized(this) {
            pool.removeFirstOrNull() ?: createSurface(context)
        }
    }

    fun release(surfaceView: SurfaceView) {
        synchronized(this) {
            // Detach from previous parent if any
            (surfaceView.parent as? ViewGroup)?.removeView(surfaceView)
            
            val maxPoolSize = sizingPolicy.calculateMaxPoolSize()
            
            if (pool.size < maxPoolSize) {
                pool.addLast(surfaceView)
            }
        }
    }

    private fun createSurface(context: Context): SurfaceView {
        return SurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
}
