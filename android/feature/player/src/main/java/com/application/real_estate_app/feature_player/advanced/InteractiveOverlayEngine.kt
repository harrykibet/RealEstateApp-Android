package com.application.real_estate_app.feature_player.advanced

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceView
import androidx.media3.common.util.UnstableApi
import com.application.real_estate_app.feature_player.ui.VideoRendererView
import javax.inject.Inject

// Live polls/shoppable tags
@UnstableApi
class InteractiveOverlayEngine @Inject constructor(
    context: Context,
    videoRendererView: VideoRendererView
) {
    private val overlaySurfaceView = SurfaceView(context)

    init {
        videoRendererView.addView(overlaySurfaceView)
        overlaySurfaceView.setZOrderMediaOverlay(true)
    }

    fun renderInteractiveElement(element: OverlayElement) {
        val canvas = overlaySurfaceView.holder.lockCanvas()
        element.draw(canvas)
        overlaySurfaceView.holder.unlockCanvasAndPost(canvas)
    }

    interface OverlayElement {
        fun draw(canvas: Canvas)
    }
}