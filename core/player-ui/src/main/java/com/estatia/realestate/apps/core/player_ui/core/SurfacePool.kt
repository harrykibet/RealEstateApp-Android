package com.estatia.realestate.apps.core.player_ui.core

import android.content.Context
import android.os.Looper
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.HdrConfiguration
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import kotlin.collections.ArrayDeque

/**
 * A thread-safe pool for reusing [SurfaceView] instances.
 * Dynamically adjusts its capacity based on [IPlayerPoolSizingPolicy].
 */
@OptIn(UnstableApi::class)
@ActivityRetainedScoped
class SurfacePool @Inject constructor(
    private val sizingPolicy: IPlayerPoolSizingPolicy,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val hdrConfiguration: HdrConfiguration
) {
    private val pool = ArrayDeque<SurfaceView>()
    private var lastContextHash: Int? = null

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("SurfacePool must only be accessed from the Main thread.")
        }
    }

    fun acquire(context: Context): SurfaceView {
        checkConfinement()
        val currentContextHash = System.identityHashCode(context)
        if (lastContextHash != null && lastContextHash != currentContextHash) {
            // Activity was recreated (config change). Evict stale surfaces to prevent leaks and artifacts.
            pool.clear()
        }
        lastContextHash = currentContextHash

        return pool.removeFirstOrNull() ?: createSurface(context)
    }

    fun release(surfaceView: SurfaceView) {
        checkConfinement()
        // Detach from previous parent if any
        (surfaceView.parent as? ViewGroup)?.removeView(surfaceView)
        
        val maxPoolSize = sizingPolicy.calculateMaxPoolSize(environmentCoordinator.environment.value)
        
        if (pool.size < maxPoolSize) {
            pool.addLast(surfaceView)
        }
    }

    private fun createSurface(context: Context): SurfaceView {
        return SurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            
            // Set PixelFormat for HDR support if available
            holder.setFormat(hdrConfiguration.getBestSupportedMode().format)
        }
    }
}
