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
    const val coreKtx = "1.15.0"
    const val appCompat = "1.7.0"
    const val material = "1.12.0"
    const val constraintLayout = "2.2.0"
    const val viewPager = "1.1.0"
    const val recyclerView = "1.4.0"
    const val swipeRefreshLayout = "1.2.0-alpha01"
    const val fragmentKtx = "1.8.6"
    const val activityKtx = "1.10.0"
    const val splashScreen = "1.2.0-alpha02"

    // ─────────────────────────────── Compose ───────────────────────────────
    const val composeBom = "2025.02.00"
    const val activityCompose = "1.10.0"
    const val composeMaterial3 = "1.4.0-alpha08"
    const val composeRuntime = "1.8.0-beta02"
    const val composeCompiler = "1.5.15"

    // ─────────────────────────────── Lifecycle & WorkManager ───────────────────────────────
    const val lifecycle = "2.9.0-alpha10"
    const val workManager = "2.10.0"

    // ─────────────────────────────── Security ───────────────────────────────
    const val securityKtx = "1.1.0-alpha06"
    const val bouncyCastle = "1.80"
    const val conscrypt = "2.5.2"

    // ─────────────────────────────── Firebase ───────────────────────────────
    const val firebaseBom = "33.9.0"

    // ─────────────────────────────── Google Cloud & Play Services ───────────────────────────────
    const val googleCloudBom = "26.55.0"
    const val playServicesMaps = "19.1.0"
    const val playServicesLocation = "21.3.0"
    const val playServicesAuth = "21.3.0"
    const val googlePlaces = "4.1.0"

    // ─────────────────────────────── Networking ───────────────────────────────
    const val okhttp = "5.0.0-alpha.14"
    const val retrofit = "2.11.0"
    const val gson = "2.12.1"

    // ─────────────────────────────── Media & Image Loading ───────────────────────────────
    const val glide = "5.0.0-rc01"
    const val media3 = "1.5.1"
    const val lottie = "6.6.2"

    // ─────────────────────────────── Google ML Kit ───────────────────────────────
    // On-Device ML Kit
    const val mlKitBarcodeScanning = "17.3.0"
    const val mlKitTextRecognition = "16.0.1"
    const val mlKitFaceDetection = "16.1.7"
    const val mlKitImageLabeling = "17.0.9"

    // Cloud-Based ML Kit
    const val mlKitCloudTextRecognition = "19.0.1"
    const val mlKitCloudImageLabeling = "16.0.8"

    // ─────────────────────────────── Dependency Injection ───────────────────────────────
    const val daggerHilt = "2.55"
    const val hiltAndroidx = "1.2.0"

    // ─────────────────────────────── Room (Database) ───────────────────────────────
    const val room = "2.7.0-beta01"

    // ─────────────────────────────── Event Bus ───────────────────────────────
    const val eventBus = "3.3.1"

    // ─────────────────────────────── Caching ───────────────────────────────
    const val caffeine = "3.2.0"

    // ─────────────────────────────── Testing ───────────────────────────────
    const val junit = "4.13.2"
    const val jupiter = "5.12.0"
    const val truth = "1.4.4"
    const val kotest = "5.9.1"
    const val mockk = "1.13.16"
    const val leakCanary = "3.0-alpha-8"

    // ─────────────── AndroidX Testing & UI Testing ───────────────
    const val testExtJunit = "1.2.1"
    const val espressoCore = "3.6.1"
    const val espressoIntents = "3.6.1"
    const val espressoContrib = "3.6.1"
    const val uiAutomator = "2.3.0"
    const val navigation = "2.9.0-alpha06"

    // ─────────────────────────────── Performance & Monitoring ───────────────────────────────
    const val androidMetrics = "1.0.0-beta01"
    const val openTelemetry = "1.47.0"
    const val micrometer = "1.15.0-M2"

    // ─────────────────────────────── Miscellaneous ───────────────────────────────
    const val ffmpeg = "6.0-2.LTS"
    const val guava = "33.4.0-jre"
    const val appSet = "16.1.0"
    const val mediaRouter = "1.7.0"
}
