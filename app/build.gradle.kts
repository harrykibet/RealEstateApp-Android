
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
    id("org.jetbrains.dokka")
    id("org.sonarqube")
  }


android {
    namespace = "com.application.real_estate_app"
    compileSdk = 35

    kapt{
        correctErrorTypes = true
        useBuildCache = true
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
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

    CoreDeps.CommonCoreDependencies.forEach { implementation(it) }
    CoreDeps.CoreUiDependencies.forEach { implementation(it) }
    implementation(CoreDeps.splashScreen)

    implementation(platform(ComposeDeps.composeBom))
    ComposeDeps.AllComposeDependencies.forEach { implementation(it) }
    ComposeDeps.ComposeDebugDependencies.forEach { debugImplementation(it) }

    LifecycleDeps.AllLifecycleDependencies.forEach { implementation(it) }

    implementation(platform(FirebaseDeps.firebaseBom))
    FirebaseDeps.AllFirebaseDependencies.forEach { implementation(it) }

    GoogleAndroidDeps.AllPlayServicesDependencies.forEach { implementation(it) }

    implementation(EventBusDeps.eventBus)

    implementation(SecurityDeps.bouncyCastle)

    NavigationDeps.AllNavigationDependencies.forEach { implementation(it) }

    HiltDeps.AllHiltDependencies.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDependencies.forEach { kapt(it) }

    implementation(MediaDeps.media3ExoPlayer)
    implementation(MediaDeps.media3UI)
    implementation(MediaDeps.lottie)
    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    DatabaseDeps.AllRoomDependencies.forEach { implementation(it) }
    DatabaseDeps.AllRoomKaptDependencies.forEach { kapt(it) }

    TestingDeps.TestDependencies.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDependencies.forEach { androidTestImplementation(it) }

    ProjectModules.AllProjectModules.forEach { implementation(project(it)) }
}

//Duplicate class conflict between:
//proto-google-common-protos-2.51.0
//protolite-well-known-types-18.0.0
configurations.all {
    resolutionStrategy.force("com.google.api.grpc:proto-google-common-protos:2.51.0")
}



