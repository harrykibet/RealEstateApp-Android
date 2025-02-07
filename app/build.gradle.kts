
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
    id("org.sonarqube")
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

        CoreDeps.commonCoreDependencies.forEach { implementation(it) }
        CoreDeps.coreUiDependencies.forEach { implementation(it) }
        implementation(CoreDeps.splashScreen)

        implementation(platform(ComposeDeps.composeBom))
        ComposeDeps.allComposeDependencies.forEach { implementation(it) }
        ComposeDeps.composeDebugDependencies.forEach { debugImplementation(it) }

        LifecycleDeps.allLifecycleDependencies.forEach { implementation(it) }

        implementation(platform(FirebaseDeps.firebaseBom))
        FirebaseDeps.allFirebaseDependencies.forEach { implementation(it) }

        GooglePlayDeps.allPlayServicesDependencies.forEach { implementation(it) }

        implementation(EventBusDeps.eventBus)

        NavigationDeps.allNavigationDependencies.forEach { implementation(it) }

        HiltDeps.allHiltDependencies.forEach { implementation(it) }
        HiltDeps.allHiltKaptDependencies.forEach { kapt(it) }

        implementation(MediaDeps.media3ExoPlayer)
        implementation(MediaDeps.media3UI)
        implementation(MediaDeps.lottie)
        implementation(MediaDeps.glide)
        kapt(MediaDeps.glideCompiler)

        DatabaseDeps.allRoomDependencies.forEach { implementation(it) }
        DatabaseDeps.allRoomKaptDependencies.forEach { kapt(it) }

        TestingDeps.TestDependencies.forEach { testImplementation(it) }
        TestingDeps.androidTestDependencies.forEach { androidTestImplementation(it) }

        ProjectModules.allProjectModules.forEach { implementation(project(it)) }
}


