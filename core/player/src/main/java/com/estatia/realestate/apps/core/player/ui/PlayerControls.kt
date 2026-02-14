package com.estatia.realestate.apps.core.player.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import com.estatia.realestate.apps.core.player.R

// Play/pause, seekbar
@Suppress("Unused")
class PlayerControls @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val playButton: ImageButton
    private val seekBar: SeekBar

    //TODO: Replace with composables
    init {
        inflate(context, R.layout.player_controls, this)
        playButton = findViewById(R.id.btnPlayPause)
        seekBar = findViewById(R.id.seekBar)
    }

    fun setPlayPauseListener(listener: (Boolean) -> Unit) {
        playButton.setOnClickListener {
            it.isSelected = !it.isSelected
            listener(it.isSelected) // Pass new state: true = playing, false = paused
        }
    }
}