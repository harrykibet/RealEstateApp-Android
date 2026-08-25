package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.common.MediaReference
import java.util.UUID

/**
 * Unified source of truth for media domain fixtures.
 */
object MediaFixtures {

    /**
     * Returns a deterministic image reference.
     */
    fun defaultImage() = MediaReference("https://estatia.com/assets/images/default.jpg")

    /**
     * Returns a deterministic video reference.
     */
    fun defaultVideo() = MediaReference("https://estatia.com/assets/videos/default.mp4")

    /**
     * Factory method for building randomized image references.
     */
    fun buildImage(id: String = UUID.randomUUID().toString()) = 
        MediaReference("https://estatia.com/assets/images/$id.jpg")

    /**
     * Factory method for building randomized video references.
     */
    fun buildVideo(id: String = UUID.randomUUID().toString()) = 
        MediaReference("https://estatia.com/assets/videos/$id.mp4")
}
