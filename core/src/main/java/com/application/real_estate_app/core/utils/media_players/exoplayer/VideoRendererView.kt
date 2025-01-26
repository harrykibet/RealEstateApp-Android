package com.application.real_estate_app.core.utils.media_players.exoplayer

import android.content.Context
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.media3.common.util.UnstableApi

// 3. Advanced Rendering System
@UnstableApi
class VideoRendererView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val surfaceView: SurfaceView
    private var player: MediaPlayer? = null
    private var surface: Surface? = null

    init {
        surfaceView = SurfaceView(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    surface = holder.surface
                    player?.setVideoSurface(surface)
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                    player?.setVideoSurfaceSize(width, height)
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    surface = null
                    player?.clearVideoSurface()
                }
            })
        }
        addView(surfaceView)
    }

    fun bindPlayer(player: MediaPlayer) {
        this.player = player
        surface?.let { player.setVideoSurface(it) }
    }
}
