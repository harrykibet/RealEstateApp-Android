/**
 * Object containing dependencies related to media handling, including Media3, FFmpeg, Glide, and Lottie.
 *
 * This object provides easy access to various media-related libraries and their specific components.
 * It includes dependencies for:
 * - Media3 (ExoPlayer, UI, HLS, etc.)
 * - Media Router
 * - FFmpeg Kit
 * - Glide (Image Loading)
 * - Lottie (Animations)
 *
 * It also provides convenience functions to retrieve specific sets of dependencies.
 */
@Suppress("constPropertyName", "MemberVisibilityCanBePrivate")
object MediaDeps {

    // Media3 Dependencies
    val media3ExoPlayer = Dependency.VersionedDependency("androidx.media3", "media3-exoplayer", Versions.media3).toGradleNotation
    val media3Database = Dependency.VersionedDependency("androidx.media3", "media3-database", Versions.media3).toGradleNotation
    val media3UI = Dependency.VersionedDependency("androidx.media3", "media3-ui", Versions.media3).toGradleNotation
    val media3Decoder = Dependency.VersionedDependency("androidx.media3", "media3-decoder", Versions.media3).toGradleNotation
    val media3Hls = Dependency.VersionedDependency("androidx.media3", "media3-exoplayer-hls", Versions.media3).toGradleNotation
    val media3Session = Dependency.VersionedDependency("androidx.media3", "media3-session", Versions.media3).toGradleNotation
    val media3Common = Dependency.VersionedDependency("androidx.media3", "media3-common", Versions.media3).toGradleNotation
    val media3DataSource = Dependency.VersionedDependency("androidx.media3", "media3-datasource", Versions.media3).toGradleNotation
    val media3Okhttp = Dependency.VersionedDependency("androidx.media3", "media3-datasource-okhttp", Versions.media3).toGradleNotation
    val media3Cronet = Dependency.VersionedDependency("androidx.media3", "media3-datasource-cronet", Versions.media3).toGradleNotation
    val media3Dash = Dependency.VersionedDependency("androidx.media3", "media3-exoplayer-dash", Versions.media3).toGradleNotation
    val media3Rtsp = Dependency.VersionedDependency("androidx.media3", "media3-exoplayer-rtsp", Versions.media3).toGradleNotation
    val media3Effect = Dependency.VersionedDependency("androidx.media3", "media3-effect", Versions.media3).toGradleNotation
    val media3Transformer = Dependency.VersionedDependency("androidx.media3", "media3-transformer", Versions.media3).toGradleNotation
    val media3Rtmp = Dependency.VersionedDependency("androidx.media3", "media3-datasource-rtmp", Versions.media3).toGradleNotation
    val media3WorkManager = Dependency.VersionedDependency("androidx.media3", "media3-exoplayer-workmanager", Versions.media3).toGradleNotation

    // Media Router
    val mediaRouter = Dependency.VersionedDependency("androidx.mediarouter", "mediarouter", Versions.mediaRouter).toGradleNotation

    // FFMPEG Kit
    val ffmpeg = Dependency.VersionedDependency("com.arthenica", "ffmpeg-kit-min-gpl", Versions.ffmpeg).toGradleNotation

    // Glide
    val glide = Dependency.VersionedDependency("com.github.bumptech.glide", "glide", Versions.glide).toGradleNotation
    val glideCompiler = Dependency.VersionedDependency("com.github.bumptech.glide", "compiler", Versions.glide).toGradleNotation

    // Lottie
    val lottie = Dependency.VersionedDependency("com.airbnb.android", "lottie", Versions.lottie).toGradleNotation

    // Functions to Retrieve Dependencies
    fun getAnimationDeps() = listOf(lottie)
    fun getImageDeps() = listOf(glide)
    fun getImageKaptDeps() = listOf(glideCompiler)

    fun getAllMedia3Deps() = listOf(
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
