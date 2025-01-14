@file:Suppress("ConstPropertyName")

object Versions {
    // Core
    const val kotlin = "1.9.10"
    const val coreKtx = "1.15.0"
    const val appCompat = "1.7.0"
    const val material = "1.12.0"
    const val constraintLayout = "2.2.0"
    const val viewPager2 = "1.1.0"
    const val recyclerView = "1.3.2"
    const val swipeRefreshLayout = "1.1.0"
    const val fragmentKtx = "1.8.5"
    const val activityKtx = "1.9.3"
    const val splashScreen = "1.0.1"

    // Compose
    const val composeBom = "2024.10.01"
    const val activityCompose = "1.9.3"
    const val composeMaterial3 = "1.3.1"
    const val composeUi = "1.5.3"
    const val composeMaterial = "1.5.3"
    const val composeUiTooling = "1.5.3"
    const val composeRuntime = "1.5."

    // Lifecycle
    const val lifecycle = "2.8.7"

    // Ffmpeg
    const val ffmpeg = "5.1.LTS"

    // Firebase
    const val firebaseBom = "33.5.1"

    // Google Play Services
    const val playServicesMaps = "19.0.0"
    const val playServicesLocation = "21.3.0"
    const val playServicesAuth = "21.2.0"
    const val places = "4.0.0"

    // Glide
    const val glide = "4.16.0"

    // Navigation
    const val navigation = "2.8.3"

    // Hilt
    const val hilt = "2.52"
    const val hiltNavigation = "1.2.0"

    // Media3 ExoPlayer
    const val media3ExoPlayer = "1.4.1"

    // Lottie
    const val lottie = "6.0.0"

    // Event Bus
    const val eventBus = "3.3.1"

    // Room
    const val room = "2.6.1"

    // GOOGLE  ML Kit On-Device
    const val mlKitBarcodeScanning = "17.1.0"
    const val mlKitTextRecognitionOnDevice = "16.0.1"
    const val mlKitFaceDetection = "16.1.7"
    const val mlKitImageLabelingOnDevice = "17.0.9"

    // GOOGLE ML Kit Cloud-Based
    const val mlKitTextRecognitionCloud = "19.0.1"
    const val mlKitImageLabelingCloud = "16.0.8"

    // JUnit
    const val junit = "4.13.2"

    // Testing
    const val testExtJUnit = "1.2.1"
    const val espressoCore = "3.6.1"
}

object Libs {
        // Core Libraries
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val viewPager2 = "androidx.viewpager2:viewpager2:${Versions.viewPager2}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayout}"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}"
        const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
        const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreen}"

        // Lifecycle
        const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
        const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"

        // Firebase
        const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
        const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
        const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"
        const val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
        const val firebaseFirestore = "com.google.firebase:firebase-firestore-ktx"
        const val firebaseStorage = "com.google.firebase:firebase-storage-ktx"
        const val playIntergrity = "com.google.firebase:firebase-appcheck-playintegrity"
        const val appCheckDebug = "com.google.firebase:firebase-appcheck-debug"

        // Google Play Services
        const val playServicesMaps = "com.google.android.gms:play-services-maps:${Versions.playServicesMaps}"
        const val playServicesLocation = "com.google.android.gms:play-services-location:${Versions.playServicesLocation}"
        const val places = "com.google.android.libraries.places:places:${Versions.places}"
        const val playServicesAuth = "com.google.android.gms:play-services-auth:${Versions.playServicesAuth}"


        // Glide
        const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
        const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

        // Compose
        const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
        const val composeUi = "androidx.compose.ui:ui"
        const val composeMaterial = "androidx.compose.material:material"
        const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val composeUiTooling = "androidx.compose.ui:ui-tooling"
        const val composeRuntimeLiveData = "androidx.compose.runtime:runtime-livedata:${Versions.composeRuntime}"
        const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
        const val composeMaterial3 = "androidx.compose.material3:material3:${Versions.composeMaterial3}"

        // Navigation
        const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
        const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"

        // Dagger Hilt
        const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
        const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
        const val hiltCompiler ="androidx.hilt:hilt-compiler:${Versions.hiltNavigation}"
        const val hiltNavigationFragment = "androidx.hilt:hilt-navigation-fragment:${Versions.hiltNavigation}"

        // Media3 ExoPlayer
        const val media3ExoPlayer = "androidx.media3:media3-exoplayer:${Versions.media3ExoPlayer}"
        const val media3UI = "androidx.media3:media3-ui:${Versions.media3ExoPlayer}"
        const val media3Hls = "androidx.media3:media3-exoplayer-hls:${Versions.media3ExoPlayer}"

        // Lottie
        const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"

       // Ffmpeg
       const val ffmpeg = "com.arthenica:ffmpeg-kit-min-gpl:${Versions.ffmpeg}"

        // Room
        const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
        const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

        // GOOGLE  ML Kit On-Device
        const val mlKitBarcodeScanning = "com.google.mlkit:barcode-scanning:${Versions.mlKitBarcodeScanning}"
        const val mlKitTextRecognitionOnDevice = "com.google.mlkit:text-recognition:${Versions.mlKitTextRecognitionOnDevice}"
        const val mlKitFaceDetection = "com.google.mlkit:face-detection:${Versions.mlKitFaceDetection}"
        const val mlKitImageLabelingOnDevice = "com.google.mlkit:image-labeling:${Versions.mlKitImageLabelingOnDevice}"

        // GOOGLE ML Kit Cloud-Based
        const val mlKitTextRecognitionCloud = "com.google.android.gms:play-services-mlkit-text-recognition:${Versions.mlKitTextRecognitionCloud}"
        const val mlKitImageLabelingCloud = "com.google.android.gms:play-services-mlkit-image-labeling:${Versions.mlKitImageLabelingCloud}"

        // JUnit
        const val junit = "junit:junit:${Versions.junit}"

        //Green Robot Event Bus
        const val eventBus = "org.greenrobot:eventbus:${Versions.eventBus}"

        // Testing
        const val testExtJUnit = "androidx.test.ext:junit:${Versions.testExtJUnit}"
        const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
}

// Usage in module build Gradle files
object ProjectModules {
    const val core = ":core"
    const val featureHome = ":feature_home"
    const val featureProperty = ":feature_property"
    const val featureAuth = ":feature_auth"
    const val featureSearch = ":feature_search"
    const val featureProfile = ":feature_profile"
    const val uiComponents = ":ui_components"
    const val network = ":network"
    const val app = ":app"
    const val localization = ":localization"
    const val security = ":security"
    const val ai_ml = ":ai_ml"
    const val featurePayments = ":feature_payments"
    const val featureMarketPlace = ":feature_marketplace"
    const val featureFavorites = ":feature_favorites"
    const val featureChats = ":feature_chats"
    const val featureNotifications = ":feature_notifications"
    const val featureComments = ":feature_comments"
    const val featureSettings = ":feature_settings"
    const val featureService = ":feature_service"
}
