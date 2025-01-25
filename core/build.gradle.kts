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
    implementation(CoreDeps.coreKtx)
    implementation(CoreDeps.appCompat)
    implementation(CoreDeps.material)
    implementation(CoreDeps.testCoreKtx)

    // Glide for Image Loading
    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    // Ffmpeg
    implementation(MediaDeps.ffmpeg)

    // Firebase
    implementation(platform(FirebaseDeps.firebaseBom)) // Firebase BOM for managing versions
    implementation(FirebaseDeps.firebaseAuth)
    implementation(FirebaseDeps.firebaseFirestore)
    implementation(FirebaseDeps.firebaseStorage)

    // Media and UI Libraries
    implementation(MediaDeps.media3UI)
    implementation(MediaDeps.media3ExoPlayer)
    implementation(MediaDeps.media3Hls)

    // Testing Libraries
    testImplementation(TestingDeps.junit)
    androidTestImplementation(TestingDeps.testExtJUnit)
    androidTestImplementation(TestingDeps.espressoCore)


    // Dagger Hilt for Dependency Injection
    implementation(HiltDeps.hiltAndroid)
    kapt(HiltDeps.hiltAndroidCompiler)
    implementation(HiltDeps.hiltNavigationFragment)
    kapt(HiltDeps.hiltCompiler)
}
