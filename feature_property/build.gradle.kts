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

    CoreDeps.commonCoreDependencies.forEach { implementation(it) }
    implementation(CoreDeps.material)

    implementation(NavigationDeps.navigationFragment)

    implementation(NetworkDeps.gson)

    DatabaseDeps.allRoomDependencies.forEach { implementation(it) }
    DatabaseDeps.allRoomKaptDependencies.forEach { kapt(it) }

    implementation(platform(FirebaseDeps.firebaseBom)) // Use BOM for version alignment
    implementation(FirebaseDeps.firebaseAuth)
    implementation(FirebaseDeps.firebaseFirestore)
    implementation(FirebaseDeps.firebaseStorage)

    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    implementation(project(ProjectModules.core))
    implementation(project(ProjectModules.uiComponents))

    implementation(GooglePlayDeps.playServicesLocation)
    implementation(GooglePlayDeps.playServicesMaps)

    LifecycleDeps.allLifecycleDependencies.forEach { implementation(it) }

    TestingDeps.TestDependencies.forEach { testImplementation(it) }
    TestingDeps.androidTestDependencies.forEach { androidTestImplementation(it) }

    HiltDeps.allHiltDependencies.forEach { implementation(it) }
    HiltDeps.allHiltKaptDependencies.forEach { kapt(it) }
}