plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.feature_profile"
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

    // Compose UI Components
    implementation(ComposeDeps.composeMaterial3)

    // Firebase Dependencies
    implementation(platform(FirebaseDeps.firebaseBom)) // Use BOM for Firebase version alignment
    implementation(FirebaseDeps.firebaseAuth)

    // Navigation Components
    implementation(NavigationDeps.navigationFragment)
    implementation(NavigationDeps.navigationUI)

    // Room
    implementation(DatabaseDeps.roomKtx)
    implementation(DatabaseDeps.roomRuntime)
    kapt(DatabaseDeps.roomCompiler)

    // Testing Libraries
    testImplementation(TestingDeps.junit)
    androidTestImplementation(TestingDeps.testExtJUnit)
    androidTestImplementation(TestingDeps.espressoCore)

    // Dagger Hilt for Dependency Injection
    implementation(HiltDeps.hiltAndroid)
    kapt(HiltDeps.hiltAndroidCompiler)
    implementation(HiltDeps.hiltNavigationFragment)
    kapt(HiltDeps.hiltCompiler)

    //Green Robot Event bus
    implementation(EventBusDeps.eventBus)

    //Modules
    implementation(project(ProjectModules.core))

    //Shared UI-Components
    implementation(project(ProjectModules.uiComponents))
}