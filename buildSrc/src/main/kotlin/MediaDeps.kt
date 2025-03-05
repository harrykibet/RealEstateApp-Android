@Suppress("constPropertyName", "MemberVisibilityCanBePrivate")
object MediaDeps {

    // Media3
    const val media3ExoPlayer = "androidx.media3:media3-exoplayer:${Versions.media3}"
    const val media3Database = "androidx.media3:media3-database:${Versions.media3}"
    const val media3UI = "androidx.media3:media3-ui:${Versions.media3}"
    const val media3Decoder = "androidx.media3:media3-decoder:${Versions.media3}"
    const val media3Hls = "androidx.media3:media3-exoplayer-hls:${Versions.media3}"
    const val media3Session = "androidx.media3:media3-session:${Versions.media3}"
    const val media3Common = "androidx.media3:media3-common:${Versions.media3}"
    const val media3DataSource = "androidx.media3:media3-datasource:${Versions.media3}"
    const val media3Okhttp = "androidx.media3:media3-datasource-okhttp:${Versions.media3}"
    const val media3Cronet = "androidx.media3:media3-datasource-cronet:${Versions.media3}"
    const val media3Dash = "androidx.media3:media3-exoplayer-dash:${Versions.media3}"
    const val media3Rtsp = "androidx.media3:media3-exoplayer-rtsp:${Versions.media3}"
    const val media3Effect = "androidx.media3:media3-effect:${Versions.media3}"
    const val media3Transformer = "androidx.media3:media3-transformer:${Versions.media3}"
    const val media3Rtmp = "androidx.media3:media3-datasource-rtmp:${Versions.media3}"
    const val media3WorkManager = "androidx.media3:media3-exoplayer-workmanager:${Versions.media3}"

    // Media Router
    const val mediaRouter = "androidx.mediarouter:mediarouter:${Versions.mediaRouter}"

    // FFMPEG Kit
    const val  ffmpeg = "com.arthenica:ffmpeg-kit-min-gpl:${Versions.ffmpeg}"

    // Glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    // Lottie
    const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"

    val AnimationDeps = listOf(
        lottie
    )

    val ImageDeps = listOf(
        glide
    )

    val ImageKaptDeps = listOf(
        glideCompiler
    )

    // Grouped Media3 Dependencies
    val AllMedia3Deps = listOf(
        media3ExoPlayer,
        media3UI,
        media3Hls,
        media3Database,
        mediaRouter,
        media3Decoder,
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
