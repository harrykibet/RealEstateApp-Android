@file:Suppress("ConstPropertyName")
object MediaDeps {
    // Ffmpeg
    const val ffmpeg = "com.arthenica:ffmpeg-kit-min-gpl:${Versions.ffmpeg}"

    // Media3 ExoPlayer
    const val media3ExoPlayer = "androidx.media3:media3-exoplayer:${Versions.media3ExoPlayer}"
    const val media3UI = "androidx.media3:media3-ui:${Versions.media3ExoPlayer}"
    const val media3Hls = "androidx.media3:media3-exoplayer-hls:${Versions.media3ExoPlayer}"
    const val media3Session = "androidx.media3:media3-session:${Versions.media3ExoPlayer}"
    const val media3Common = "androidx.media3:media3-common:${Versions.media3ExoPlayer}"
    const val media3DataSource = "androidx.media3:media3-datasource:${Versions.media3ExoPlayer}"
    const val media3Okhttp = "androidx.media3:media3-datasource-okhttp:${Versions.media3ExoPlayer}"
    const val media3Cronet = "androidx.media3:media3-datasource-cronet:${Versions.media3ExoPlayer}"
    const val media3Dash = "androidx.media3:media3-exoplayer-dash:${Versions.media3ExoPlayer}"
    const val media3Rtsp = "androidx.media3:media3-exoplayer-rtsp:${Versions.media3ExoPlayer}"
    const val media3Effect = "androidx.media3:media3-effect:${Versions.media3ExoPlayer}"
    const val media3Transformer = "androidx.media3:media3-transformer:${Versions.media3ExoPlayer}"
    const val media3Rtmp = "androidx.media3:media3-datasource-rtmp:${Versions.media3ExoPlayer}"
    const val media3WorkManager = "androidx.media3:media3-exoplayer-workmanager:${Versions.media3ExoPlayer}"

    // Glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    // Lottie
    const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"

    // Grouped Media3 Dependencies
    val allMedia3Dependencies = listOf(
        media3ExoPlayer,
        media3UI,
        media3Hls,
        media3Session,
        media3Common,
        media3DataSource,
        media3Okhttp,
        media3Cronet,
        media3Dash,
        media3Rtsp,
        media3Effect,
        media3Transformer,
        media3Rtmp,
        media3WorkManager
    )
}
