plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.feature_search"
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
    implementation(CoreDeps.appCompat)
    implementation(CoreDeps.material)

    // Google Play Services
    implementation(GooglePlayDeps.playServicesMaps)
    implementation(GooglePlayDeps.playServicesLocation)
    implementation(GooglePlayDeps.places)

    // Firebase Services
    implementation(platform(FirebaseDeps.firebaseBom)) // Version alignment for Firebase libraries
    implementation(FirebaseDeps.firebaseFirestore)

    // Navigation Components
    implementation(NavigationDeps.navigationFragment)
    implementation(NavigationDeps.navigationUI)

    // Testing Libraries
    testImplementation(TestingDeps.junit)
    androidTestImplementation(TestingDeps.testExtJUnit)
    androidTestImplementation(TestingDeps.espressoCore)

    // Room
    implementation(RoomDeps.roomKtx)
    implementation(RoomDeps.roomRuntime)
    kapt(RoomDeps.roomCompiler)

    //Core module
    implementation(project(ProjectModules.core))

    //Shared UI-Components
    implementation(project(ProjectModules.uiComponents))

    // Dagger Hilt for Dependency Injection
    implementation(HiltDeps.hiltAndroid)
    kapt(HiltDeps.hiltAndroidCompiler)
    implementation(HiltDeps.hiltNavigationFragment)
    kapt(HiltDeps.hiltCompiler)
}