
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
        implementation(CoreDeps.coreKtx)
        implementation(CoreDeps.appCompat)
        implementation(CoreDeps.material)
        implementation(CoreDeps.constraintLayout)
        implementation(CoreDeps.viewPager2)
        implementation(CoreDeps.recyclerView)
        implementation(CoreDeps.swipeRefreshLayout)
        implementation(CoreDeps.splashScreen)

        //Compose dependencies
        implementation(platform(ComposeDeps.composeBom))
        implementation(ComposeDeps.composeUi)
        implementation(ComposeDeps.composeMaterial)
        implementation(ComposeDeps.composeUiToolingPreview)
        debugImplementation(ComposeDeps.composeUiTooling)
        implementation(ComposeDeps.composeRuntimeLiveData)
        implementation(ComposeDeps.activityCompose)
        implementation(ComposeDeps.composeMaterial3)


        // Lifecycle
        implementation(LifecycleDeps.lifecycleRuntime)
        implementation(LifecycleDeps.viewModelKtx)
        implementation(LifecycleDeps.liveDataKtx)

        // Firebase BoM and dependencies
        implementation(platform(FirebaseDeps.firebaseBom))
        implementation(FirebaseDeps.firebaseAnalytics)
        implementation(FirebaseDeps.firebaseCrashlytics)
        implementation(FirebaseDeps.firebaseAuth)
        implementation(FirebaseDeps.firebaseFirestore)
        implementation(FirebaseDeps.firebaseStorage)
        implementation(FirebaseDeps.appCheckDebug)
        implementation(FirebaseDeps.playIntergrity)

        // Google Play Services
        implementation(GooglePlayDeps.playServicesMaps)
        implementation(GooglePlayDeps.playServicesLocation)
        implementation(GooglePlayDeps.places)


        // Glide
        implementation(MediaDeps.glide)
        kapt(MediaDeps.glideCompiler)

        // Green Robot Event Bus
        implementation(EventBusDeps.eventBus)

        // Navigation Component
        implementation(NavigationDeps.navigationFragment)
        implementation(NavigationDeps.navigationUI)

        // Dagger Hilt
        implementation(HiltDeps.hiltAndroid)
        kapt(HiltDeps.hiltAndroidCompiler)
        implementation(HiltDeps.hiltNavigationFragment)
        kapt(HiltDeps.hiltCompiler)

        // Media3 ExoPlayer
        implementation(MediaDeps.media3ExoPlayer)
        implementation(MediaDeps.media3UI)

        // Lottie
        implementation(MediaDeps.lottie)

        // Room
        implementation(RoomDeps.roomKtx)
        implementation(RoomDeps.roomRuntime)
        kapt(RoomDeps.roomCompiler)


       // Testing Libraries
       testImplementation(TestingDeps.junit)
       androidTestImplementation(TestingDeps.testExtJUnit)
       androidTestImplementation(TestingDeps.espressoCore)

        // Project Modules
        implementation(project(ProjectModules.featureHome))
        implementation(project(ProjectModules.core))
        implementation(project(ProjectModules.featureProperty))
        implementation(project(ProjectModules.featureAuth))
        implementation(project(ProjectModules.featureSearch))
        implementation(project(ProjectModules.featureProfile))
        implementation(project(ProjectModules.uiComponents))
        implementation(project(ProjectModules.featureIntelligence))
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


