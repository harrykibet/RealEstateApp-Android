package com.estatia.realestate.apps.core.model.common

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * A domain-agnostic reference to a media file (image, video, etc.).
 * Wraps an underlying string representation (e.g., a file path or URI string)
 * to avoid leaking platform-specific types like android.net.Uri into the domain layer.
 */
@Serializable
@JvmInline
value class MediaReference(val value: String)
