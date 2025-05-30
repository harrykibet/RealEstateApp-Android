package com.estatia.realestate.apps.feature.player.ui

import android.view.View
import android.view.animation.Animation
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.feature.player.ui.VideoRendererView
import javax.inject.Inject

// Captions, stickers
@Suppress("Unused")
@UnstableApi
class OverlayManager @Inject constructor(
    private val videoRendererView: VideoRendererView
) {
    private val overlays = mutableMapOf<String, View>()

    fun addOverlay(id: String, view: View) {
        overlays[id] = view
        videoRendererView.addView(view)
    }

    fun animateOverlay(id: String, animation: Animation) {
        overlays[id]?.startAnimation(animation)
    }
}