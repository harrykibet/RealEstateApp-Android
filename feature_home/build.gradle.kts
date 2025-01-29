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
    implementation(CoreDeps.coreKtx)
    implementation(CoreDeps.constraintLayout)
    implementation(CoreDeps.recyclerView)
    implementation(CoreDeps.viewPager2)
    implementation(CoreDeps.appCompat)
    implementation(CoreDeps.material)
    implementation(CoreDeps.swipeRefreshLayout)
    implementation(CoreDeps.fragmentKtx)
    implementation(CoreDeps.activityKtx)

    // Compose UI Components
    implementation(ComposeDeps.composeMaterial3)

    // Media and UI Libraries
    implementation(MediaDeps.media3UI)
    implementation(MediaDeps.media3ExoPlayer)
    implementation(MediaDeps.media3Hls)

    // Glide for Image Loading
    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    // Firebase Dependencies
    implementation(platform(FirebaseDeps.firebaseBom)) // Use BOM for Firebase version alignment
    implementation(FirebaseDeps.firebaseAuth)
    implementation(FirebaseDeps.firebaseFirestore)
    implementation(FirebaseDeps.firebaseStorage)

    // Project Module Dependencies
    implementation(project(ProjectModules.core))

    //Shared UI-Components
    implementation(project(ProjectModules.uiComponents))

    // Room
    implementation(DatabaseDeps.roomKtx)
    implementation(DatabaseDeps.roomRuntime)
    kapt(DatabaseDeps.roomCompiler)

    // Navigation Components
    implementation(NavigationDeps.navigationFragment)
    implementation(NavigationDeps.navigationUI)

    // Lifecycle Components
    implementation(LifecycleDeps.viewModelKtx)
    implementation(LifecycleDeps.liveDataKtx)

    // Testing Libraries
    testImplementation(TestingDeps.junit)
    androidTestImplementation(TestingDeps.testExtJUnit)
    androidTestImplementation(TestingDeps.espressoCore)

    // Hilt for Dependency Injection
    implementation(HiltDeps.hiltAndroid)
    kapt(HiltDeps.hiltAndroidCompiler)
    implementation(HiltDeps.hiltNavigationFragment)
    kapt(HiltDeps.hiltCompiler)
}