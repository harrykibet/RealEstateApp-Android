package com.estatia.realestate.apps.feature.player.ui


import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.common.system.DeviceUtils
import com.estatia.realestate.apps.feature.player.streaming.HdrConfiguration
import javax.inject.Inject

@Suppress("Unused")
@UnstableApi
class VideoRendererView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : PlayerView(context, attrs) {

    @Inject
    lateinit var deviceUtils: DeviceUtils
    @Inject
    lateinit var hdrConfiguration: HdrConfiguration

    private val overlayContainer: FrameLayout by lazy {
        FrameLayout(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }
    }

    init {
        useController = false
        setShutterBackgroundColor(Color.TRANSPARENT)
        addView(overlayContainer) // Add overlay container to player view
    }


    fun enableHDR() {
        //Check if the device supports HDR
        if (deviceUtils.supports10BitHdr()) {
            val surfaceView = SurfaceView(context).apply {
                setFormatCompat()
            }

            player?.setVideoSurfaceView(surfaceView)
            overlayContainer.bringToFront()
        }
    }


    private fun SurfaceView.setFormatCompat() {
        holder.setFormat(PixelFormat.RGBA_1010102)
    }

    private fun createFallbackSurface() {
        player?.setVideoSurfaceView(SurfaceView(context).apply {
            holder.setFormat(PixelFormat.RGB_565)
        })
    }

    override fun addView(child: View?) {
        if (child != null) {
            overlayContainer.addView(child)
        }
    }

    fun addView(view: View, params: LayoutParams) {
        overlayContainer.addView(view, params)
    }

    override fun removeView(view: View) {
        overlayContainer.removeView(view)
    }

    fun clearOverlays() {
        overlayContainer.removeAllViews()
    }

    fun setOverlayTouchEnabled(enabled: Boolean) {
        overlayContainer.isClickable = enabled
        overlayContainer.isFocusable = enabled
    }

    fun bringOverlayToFront(view: View) {
        overlayContainer.bringChildToFront(view)
    }

    fun setOverlayRenderingMode(hardwareAccelerated: Boolean) {
        overlayContainer.setLayerType(
            if (hardwareAccelerated) LAYER_TYPE_HARDWARE else LAYER_TYPE_SOFTWARE,
            null
        )
    }
}