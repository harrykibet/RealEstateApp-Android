plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.feature_property"
    compileSdk = 35

    kapt{
        correctErrorTypes = true
        useBuildCache = true
    }

    buildFeatures {
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
    implementation(CoreDeps.activityKtx)
    implementation(CoreDeps.fragmentKtx)

    // Navigation Components
    implementation(NavigationDeps.navigationFragment)

    //GSON
    implementation(NetworkDeps.gson)

    // Room
    implementation(DatabaseDeps.roomKtx)
    implementation(DatabaseDeps.roomRuntime)
    kapt(DatabaseDeps.roomCompiler)

    // Firebase Dependencies
    implementation(platform(FirebaseDeps.firebaseBom)) // Use BOM for version alignment
    implementation(FirebaseDeps.firebaseAuth)
    implementation(FirebaseDeps.firebaseFirestore)
    implementation(FirebaseDeps.firebaseStorage)

    //Glide for image loading
    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    //Core module
    implementation(project(ProjectModules.core))

    //Shared UI-Components
    implementation(project(ProjectModules.uiComponents))

    // Google Play Services
    implementation(GooglePlayDeps.playServicesLocation)
    implementation(GooglePlayDeps.playServicesMaps)

    // Lifecycle Components
    implementation(LifecycleDeps.viewModelKtx)
    implementation(LifecycleDeps.liveDataKtx)

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