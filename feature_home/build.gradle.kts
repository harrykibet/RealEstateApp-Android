plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.application.real_estate_app.feature_home"
    compileSdk = 35

    kapt{
        correctErrorTypes = true
        useBuildCache = true
    }

    buildFeatures{
        viewBinding = true
        dataBinding = true
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
    implementation(Libs.constraintLayout)
    implementation(Libs.recyclerView)
    implementation(Libs.viewPager2)
    implementation(Libs.appCompat)
    implementation(Libs.material)
    implementation(Libs.swipeRefreshLayout)

    // Compose UI Components
    implementation(Libs.composeMaterial3)

    // Media and UI Libraries
    implementation(Libs.media3UI)
    implementation(Libs.media3ExoPlayer)
    implementation(Libs.media3Hls)

    // Glide for Image Loading
    implementation(Libs.glide)
    kapt(Libs.glideCompiler)

    // Firebase Dependencies
    implementation(platform(Libs.firebaseBom)) // Use BOM for Firebase version alignment
    implementation(Libs.firebaseAuth)
    implementation(Libs.firebaseFirestore)
    implementation(Libs.firebaseStorage)

    // Project Module Dependencies
    implementation(project(ProjectModules.domain))
    implementation(project(ProjectModules.core))

    // Navigation Components
    implementation(Libs.navigationFragment)
    implementation(Libs.navigationUI)

    // Lifecycle Components
    implementation(Libs.viewModelKtx)
    implementation(Libs.liveDataKtx)
    implementation(Libs.fragmentKtx)
    implementation(Libs.activityKtx)

    // Testing Libraries
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.testExtJUnit)
    androidTestImplementation(Libs.espressoCore)

    // Hilt for Dependency Injection
    implementation(Libs.hiltAndroid)
    kapt(Libs.hiltAndroidCompiler)
    implementation(Libs.hiltNavigationFragment)
    kapt(Libs.hiltCompiler)
}