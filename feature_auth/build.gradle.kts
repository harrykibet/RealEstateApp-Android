plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.feature_auth"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

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

    // Firebase Dependencies
    implementation(platform(FirebaseDeps.firebaseBom)) // Using Firebase BOM for consistent versioning
    implementation(FirebaseDeps.firebaseAuth)
    implementation(FirebaseDeps.firebaseFirestore)

    //Google Play Services
    implementation(GooglePlayDeps.playServicesAuth)

    // Project Module Dependencies
    implementation(project(ProjectModules.core))

    // Room
    implementation(RoomDeps.roomKtx)
    implementation(RoomDeps.roomRuntime)
    kapt(RoomDeps.roomCompiler)

    // Navigation Components
    implementation(NavigationDeps.navigationFragment)
    implementation(NavigationDeps.navigationUI)

    //Shared UI-Components
    implementation(project(ProjectModules.uiComponents))

    // Event Bus
    implementation(EventBusDeps.eventBus)

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
