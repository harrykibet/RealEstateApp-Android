package com.estatia.realestate.apps.feature.player.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import com.application.real_estate_app.feature_player.R

// Play/pause, seekbar
@Suppress("Unused")
class PlayerControls @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val playButton: ImageButton
    private val seekBar: SeekBar

    init {
        inflate(context, R.layout.player_controls, this)
        playButton = findViewById(R.id.btnPlayPause)
        seekBar = findViewById(R.id.seekBar)
    }

    fun setPlayPauseListener(listener: (Boolean) -> Unit) {
        playButton.setOnClickListener {
            listener(it.isSelected)
            it.isSelected = !it.isSelected
        }
    }
}