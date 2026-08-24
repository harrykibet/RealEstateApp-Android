package com.estatia.realestate.apps.core.testing.generators

import com.estatia.realestate.apps.core.model.common.MediaReference
import java.util.UUID

/**
 * Generator for domain media references.
 */
object MediaGenerator {
    fun generateImage(): MediaReference = 
        MediaReference("https://estatia.com/assets/images/${UUID.randomUUID()}.jpg")

    fun generateVideo(): MediaReference = 
        MediaReference("https://estatia.com/assets/videos/${UUID.randomUUID()}.mp4")
}
