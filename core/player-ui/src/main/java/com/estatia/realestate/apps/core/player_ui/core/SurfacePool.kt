package com.estatia.realestate.apps.core.player_ui.core

import android.content.Context
import android.view.SurfaceView
import android.view.ViewGroup
import kotlin.collections.ArrayDeque

/**
 * A simple thread-safe pool for reusing [SurfaceView] instances.
 * This prevents the expensive allocation of new window layers during feed scrolling.
 */
object SurfacePool {
    private val pool = ArrayDeque<SurfaceView>()
    private const val MAX_POOL_SIZE = 5

    fun acquire(context: Context): SurfaceView {
        return synchronized(this) {
            pool.removeFirstOrNull() ?: createSurface(context)
        }
    }

    fun release(surfaceView: SurfaceView) {
        synchronized(this) {
            // Detach from previous parent if any
            (surfaceView.parent as? ViewGroup)?.removeView(surfaceView)
            
            if (pool.size < MAX_POOL_SIZE) {
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
