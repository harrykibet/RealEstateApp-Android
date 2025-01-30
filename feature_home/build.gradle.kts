plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.application.real_estate_app.feature_home"
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

    CoreDeps.commonCoreDependencies.forEach { implementation(it) }
    CoreDeps.coreUiDependencies.forEach { implementation(it) }

    implementation(ComposeDeps.composeBom)
    implementation(ComposeDeps.composeMaterial3)

    implementation(MediaDeps.media3UI)
    implementation(MediaDeps.media3ExoPlayer)
    implementation(MediaDeps.media3Hls)
    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    implementation(platform(FirebaseDeps.firebaseBom)) // Use BOM for Firebase version alignment
    implementation(FirebaseDeps.firebaseAuth)
    implementation(FirebaseDeps.firebaseFirestore)
    implementation(FirebaseDeps.firebaseStorage)

    implementation(project(ProjectModules.core))
    implementation(project(ProjectModules.uiComponents))

    DatabaseDeps.allRoomDependencies.forEach { implementation(it) }
    DatabaseDeps.allRoomKaptDependencies.forEach { kapt(it) }

    NavigationDeps.allNavigationDependencies.forEach { implementation(it) }

    LifecycleDeps.allLifecycleDependencies.forEach { implementation(it) }

    TestingDeps.TestDependencies.forEach { testImplementation(it) }
    TestingDeps.androidTestDependencies.forEach { androidTestImplementation(it) }

    HiltDeps.allHiltDependencies.forEach { implementation(it) }
    HiltDeps.allHiltKaptDependencies.forEach { kapt(it) }
}