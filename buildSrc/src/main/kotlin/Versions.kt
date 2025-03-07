/**
 * Object containing version numbers for various dependencies used in the project.
 *
 * This object centralizes the management of library versions, making it easier to update
 * dependencies across the project and ensuring consistency. Each constant represents
 * the version of a specific library.
 */
@Suppress("ConstPropertyName")
object Versions {

    // ─────────────────────────────── Core Libraries ───────────────────────────────
    const val coreKtx = "_"
    const val testCoreKtx = "_"
    const val appCompat = "_"
    const val material = "_"
    const val constraintLayout = "_"
    const val viewPager = "_"
    const val recyclerView = "_"
    const val swipeRefreshLayout = "_"
    const val fragmentKtx = "_"
    const val activityKtx = "_"
    const val splashScreen = "_"

    // ─────────────────────────────── Compose ───────────────────────────────
    const val composeBom = "_"
    const val activityCompose = "_"
    const val composeMaterial3 = "_"
    const val composeRuntime = "_"
    const val composeCompiler = "_"

    // ─────────────────────────────── Lifecycle & WorkManager ───────────────────────────────
    const val lifecycle = "_"
    const val workManager = "_"

    // ─────────────────────────────── Security ───────────────────────────────
    const val securityKtx = "_"
    const val bouncyCastle = "_"
    const val conscrypt = "_"

    // ─────────────────────────────── Firebase ───────────────────────────────
    const val firebaseBom = "_"

    // ─────────────────────────────── Google Cloud & Play Services ───────────────────────────────
    const val googleCloudBom = "_"
    const val playServicesMaps = "_"
    const val playServicesLocation = "_"
    const val playServicesAuth = "_"
    const val googlePlaces = "_"

    // ─────────────────────────────── Networking ───────────────────────────────
    const val okhttp = "_"
    const val retrofit = "_"
    const val gson = "_"

    // ─────────────────────────────── Media & Image Loading ───────────────────────────────
    const val glide = "_"
    const val media3 = "_"
    const val lottie = "_"

    // ─────────────────────────────── Google ML Kit ───────────────────────────────
    // On-Device ML Kit
    const val mlKitBarcodeScanning = "_"
    const val mlKitTextRecognition = "_"
    const val mlKitFaceDetection = "_"
    const val mlKitImageLabeling = "_"

    // Cloud-Based ML Kit
    const val mlKitCloudTextRecognition = "_"
    const val mlKitCloudImageLabeling = "_"

    // ─────────────────────────────── Dependency Injection ───────────────────────────────
    const val daggerHilt = "_"
    const val hiltAndroidx = "_"

    // ─────────────────────────────── Room (Database) ───────────────────────────────
    const val room = "_"

    // ─────────────────────────────── Event Bus ───────────────────────────────
    const val eventBus = "_"

    // ─────────────────────────────── Caching ───────────────────────────────
    const val caffeine = "_"

    // ─────────────────────────────── Testing ───────────────────────────────
    const val junit = "_"
    const val jupiter = "_"
    const val truth = "_"
    const val kotest = "_"
    const val mockk = "_"
    const val leakCanary = "_"
    const val coreTesting = "_"

    // ─────────────── AndroidX Testing & UI Testing ───────────────
    const val testExtJunit = "_"
    const val espressoCore = "_"
    const val espressoIntents = "_"
    const val espressoContrib = "_"
    const val uiAutomator = "_"
    const val navigation = "_"

    // ─────────────────────────────── Performance & Monitoring ───────────────────────────────
    const val androidMetrics = "_"
    const val openTelemetry = "_"
    const val micrometer = "_"

    // ─────────────────────────────── Miscellaneous ───────────────────────────────
    const val ffmpeg = "6.0-2.LTS"
    const val guava = "33.4.0-jre"
    const val protobuf = "_"
    const val appSet = "_"
    const val mediaRouter = "_"
}
