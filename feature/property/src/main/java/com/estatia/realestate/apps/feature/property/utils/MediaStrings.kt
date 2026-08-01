package com.estatia.realestate.apps.feature.property.utils


object MediaStrings {
    const val IMAGE_MIME_TYPE = "image/*"
    const val VIDEO_MIME_TYPE = "video/*"
    const val ALL_FILES_TYPE = "*/*"
    const val MEDIA_PICKER_TITLE = "Select Media"
    const val FILE_SIZE_LIMIT_EXCEEDED_MESSAGE = "File size exceeds 50MB, cannot upload."
    const val FILE_READ_MODE = "r"
    const val MEDIA_TYPE_IMAGE = "image/"
    const val MEDIA_TYPE_VIDEO = "video/"
    const val MEDIA_TYPE_IMAGES = "images"
    const val MEDIA_TYPE_VIDEOS = "videos"
    const val EXTERNAL_FILES_URI = "external"
    const val URI_FILE_EXTENSION_DELIMITER = "."
    const val ERROR_PICTURES_DIR = "MediaLoader: Default pictures directory not found or is empty."
    const val MEDIA_SELECTION_PROMPT = "Please select at least one media file"

    object FileExtensions {
        const val JPG = "jpg"
        const val JPEG = "jpeg"
        const val PNG = "png"
        const val MP4 = "mp4"
        const val MKV = "mkv"
        const val AVI = "avi"
        const val MOV = "mov"
        const val WEBM = "webm"
        const val FLV = "flv"
    }
}
