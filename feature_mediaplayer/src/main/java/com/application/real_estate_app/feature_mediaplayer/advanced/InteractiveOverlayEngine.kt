package com.application.real_estate_app.feature_mediaplayer.advanced

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceView
import com.application.real_estate_app.feature_mediaplayer.ui.VideoRendererView
import javax.inject.Inject

// Live polls/shoppable tags
class InteractiveOverlayEngine @Inject constructor(
    private val context: Context,
    private val videoRendererView: VideoRendererView
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