plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.ai_ml"
    compileSdk = 35

    kapt{
        correctErrorTypes = true
        useBuildCache = true
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    //Core libraries
    implementation(Libs.coreKtx)
    implementation(Libs.material)
    implementation(Libs.appCompat)
    implementation(Libs.activityKtx)

    // Lifecycle Components
    implementation(Libs.viewModelKtx)
    implementation(Libs.liveDataKtx)
    implementation(Libs.fragmentKtx)

    // GOOGLE ML Kit libraries
    // Device-based
    implementation(Libs.mlKitBarcodeScanning)
    implementation(Libs.mlKitTextRecognitionOnDevice)
    implementation(Libs.mlKitFaceDetection)
    implementation(Libs.mlKitImageLabelingOnDevice)
    // Cloud-based
    implementation(Libs.mlKitTextRecognitionCloud)
    implementation(Libs.mlKitImageLabelingCloud)

    // Dagger Hilt
    implementation(Libs.hiltAndroid)
    kapt(Libs.hiltAndroidCompiler)
    implementation(Libs.hiltNavigationFragment)
    kapt(Libs.hiltCompiler)


    // Testing libraries
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.testExtJUnit)
    androidTestImplementation(Libs.espressoCore)
}