package com.application.real_estate_app.feature_mediaplayer.ui

import android.view.View
import android.view.animation.Animation
import javax.inject.Inject

// Captions, stickers
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