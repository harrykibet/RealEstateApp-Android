plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.feature_chats"
    compileSdk = 34

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
    implementation(Libs.appCompat)
    implementation(Libs.material)

    // Testing Libraries
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.testExtJUnit)
    androidTestImplementation(Libs.espressoCore)

    //Core module
    implementation(project(ProjectModules.core))

    // Room
    implementation(Libs.roomKtx)
    implementation(Libs.roomRuntime)
    kapt(Libs.roomCompiler)

    //Shared UI-Components
    implementation(project(ProjectModules.uiComponents))

    // Dagger Hilt for Dependency Injection
    implementation(Libs.hiltAndroid)
    kapt(Libs.hiltAndroidCompiler)
    implementation(Libs.hiltNavigationFragment)
    kapt(Libs.hiltCompiler)
}