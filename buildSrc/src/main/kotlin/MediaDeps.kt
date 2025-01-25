
@file:Suppress("ConstPropertyName")
object MediaDeps {
    // Ffmpeg
    const val ffmpeg = "com.arthenica:ffmpeg-kit-min-gpl:${Versions.ffmpeg}"

    // Media3 ExoPlayer
    const val media3ExoPlayer = "androidx.media3:media3-exoplayer:${Versions.media3ExoPlayer}"
    const val media3UI = "androidx.media3:media3-ui:${Versions.media3ExoPlayer}"
    const val media3Hls = "androidx.media3:media3-exoplayer-hls:${Versions.media3ExoPlayer}"

    // Glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    // Lottie
    const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"
}