plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.feature_intelligence"
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

    //Core libraries
    implementation(CoreDeps.coreKtx)
    implementation(CoreDeps.material)
    implementation(CoreDeps.appCompat)
    implementation(CoreDeps.activityKtx)
    implementation(CoreDeps.fragmentKtx)

    // Lifecycle Components
    implementation(LifecycleDeps.viewModelKtx)
    implementation(LifecycleDeps.liveDataKtx)


    // GOOGLE ML Kit libraries
    MLKitDeps.allMlKitDependencies.forEach {
        implementation(it)
    }

    //Shared UI-Components
    implementation(project(ProjectModules.uiComponents))

    // Room
    implementation(DatabaseDeps.roomRuntime)
    implementation(DatabaseDeps.roomKtx)
    kapt(DatabaseDeps.roomCompiler)

    // Dagger Hilt
    implementation(HiltDeps.hiltAndroid)
    kapt(HiltDeps.hiltAndroidCompiler)
    implementation(HiltDeps.hiltNavigationFragment)
    kapt(HiltDeps.hiltCompiler)


    // Testing libraries
    testImplementation(TestingDeps.junit)
    androidTestImplementation(TestingDeps.testExtJUnit)
    androidTestImplementation(TestingDeps.espressoCore)
}