plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.feature_favorites"
    compileSdk = 34

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

    // Testing Libraries
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.testExtJUnit)
    androidTestImplementation(Libs.espressoCore)

    // SwipeRefreshLayout
    implementation(Libs.swipeRefreshLayout)

    // Glide for Image Loading
    implementation(Libs.glide)
    kapt(Libs.glideCompiler)

    // Media and UI Libraries
    implementation(Libs.media3UI)
    implementation(Libs.media3ExoPlayer)
    implementation(Libs.media3Hls)

    //Shared UI-Components
    implementation(project(ProjectModules.uiComponents))

    // Firebase Dependencies
    implementation(platform(Libs.firebaseBom)) // Use BOM for version alignment
    implementation(Libs.firebaseFirestore)

    //Core module
    implementation(project(ProjectModules.core))

    // Dagger Hilt for Dependency Injection
    implementation(Libs.hiltAndroid)
    kapt(Libs.hiltAndroidCompiler)
    implementation(Libs.hiltNavigationFragment)
    kapt(Libs.hiltCompiler)
}