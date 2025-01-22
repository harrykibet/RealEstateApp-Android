plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.core"
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


    // Core Android Libraries
    implementation(Libs.coreKtx)
    implementation(Libs.appCompat)
    implementation(Libs.material)

    // Glide for Image Loading
    implementation(Libs.glide)
    implementation("androidx.test:core-ktx:1.6.1")
    kapt(Libs.glideCompiler)

    // Ffmpeg
    implementation(Libs.ffmpeg)

    // Firebase
    implementation(platform(Libs.firebaseBom)) // Firebase BOM for managing versions
    implementation(Libs.firebaseAuth)
    implementation(Libs.firebaseFirestore)
    implementation(Libs.firebaseStorage)

    // Media and UI Libraries
    implementation(Libs.media3UI)
    implementation(Libs.media3ExoPlayer)
    implementation(Libs.media3Hls)

    // Testing Libraries
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.testExtJUnit)
    androidTestImplementation(Libs.espressoCore)
    androidTestImplementation(Libs.testCoreKtx)

    // Dagger Hilt for Dependency Injection
    implementation(Libs.hiltAndroid)
    kapt(Libs.hiltAndroidCompiler)
    implementation(Libs.hiltNavigationFragment)
    kapt(Libs.hiltCompiler)
}
