package com.application.real_estate_app.feature_mediaplayer.ui

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.SurfaceView

// HDR/Dolby Vision rendering
class VideoRendererView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : StyledPlayerView(context, attrs) {

    init {
        useController = false
        setShutterBackgroundColor(Color.TRANSPARENT)
    }

    fun enableHDR(hdrMode: HDRMode = HDRMode.HDR10) {
        player?.setVideoSurfaceView(
            SurfaceView(context).apply {
                holder.setFormat(PixelFormat.RGBA_1010102)
            }
        )
    }

    fun addView(view: SurfaceView) {
    }

    enum class HDRMode { HDR10, DOLBY_VISION }
}