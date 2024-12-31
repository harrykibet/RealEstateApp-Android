
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
      // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
      // Add the Crashlytics gradle plugin
    id("com.google.firebase.crashlytics")
    // Add the Secrets Gradle plugin
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    // Add the Safe Args Gradle plugin
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.kotlin.kapt")
    //Dagger Hilt for Dependencies Injection
    id("com.google.dagger.hilt.android")
  }


android {
    namespace = "com.application.real_estate_app"
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
        applicationId = "com.application.real_estate_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
        // Core Libraries
        implementation(Libs.coreKtx)
        implementation(Libs.appCompat)
        implementation(Libs.material)
        implementation(Libs.constraintLayout)
        implementation(Libs.viewPager2)
        implementation(Libs.recyclerView)
        implementation(Libs.swipeRefreshLayout)

        //Compose dependencies
        implementation(platform(Libs.composeBom))
        implementation(Libs.composeUi)
        implementation(Libs.composeMaterial)
        implementation(Libs.composeUiToolingPreview)
        implementation(Libs.splashScreen)
        debugImplementation(Libs.composeUiTooling)
        implementation(Libs.composeRuntimeLiveData)
        implementation(Libs.activityCompose)
        implementation(Libs.composeMaterial3)


        // Lifecycle
        implementation(Libs.lifecycleRuntime)
        implementation(Libs.viewModelKtx)
        implementation(Libs.liveDataKtx)

        // Firebase BoM and dependencies
        implementation(platform(Libs.firebaseBom))
        implementation(Libs.firebaseAnalytics)
        implementation(Libs.firebaseCrashlytics)
        implementation(Libs.firebaseAuth)
        implementation(Libs.firebaseFirestore)
        implementation(Libs.firebaseStorage)
        implementation(Libs.appCheckDebug)
        implementation(Libs.playIntergrity)

        // Google Play Services
        implementation(Libs.playServicesMaps)
        implementation(Libs.playServicesLocation)
        implementation(Libs.places)


        // Glide
        implementation(Libs.glide)
        kapt(Libs.glideCompiler)

        // Green Robot Event Bus
        implementation(Libs.eventBus)

        // Navigation Component
        implementation(Libs.navigationFragment)
        implementation(Libs.navigationUI)

        // Dagger Hilt
        implementation(Libs.hiltAndroid)
        kapt(Libs.hiltAndroidCompiler)
        implementation(Libs.hiltNavigationFragment)
        kapt(Libs.hiltCompiler)

        // Media3 ExoPlayer
        implementation(Libs.media3ExoPlayer)
        implementation(Libs.media3UI)

        // Lottie
        implementation(Libs.lottie)

        // Room
        implementation(Libs.roomRuntime)
        kapt(Libs.roomCompiler)


       // Testing Libraries
       testImplementation(Libs.junit)
       androidTestImplementation(Libs.testExtJUnit)
       androidTestImplementation(Libs.espressoCore)

        // Project Modules
        implementation(project(ProjectModules.featureHome))
        implementation(project(ProjectModules.data))
        implementation(project(ProjectModules.core))
        implementation(project(ProjectModules.domain))
        implementation(project(ProjectModules.network))
        implementation(project(ProjectModules.featureProperty))
        implementation(project(ProjectModules.featureAuth))
        implementation(project(ProjectModules.featureSearch))
        implementation(project(ProjectModules.featureProfile))
        implementation(project(ProjectModules.uiComponents))
        implementation(project(ProjectModules.ai_ml))
        implementation(project(ProjectModules.featurePayments))
        implementation(project(ProjectModules.featureMarketPlace))
        implementation(project(ProjectModules.featureChats))
        implementation(project(ProjectModules.security))
        implementation(project(ProjectModules.featureNotifications))
        implementation(project(ProjectModules.localization))
        implementation(project(ProjectModules.featureFavorites))
        implementation(project(ProjectModules.featureComments))
        implementation(project(ProjectModules.featureSettings))
        implementation(project(ProjectModules.featureService))
}


