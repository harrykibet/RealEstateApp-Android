plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.application.real_estate_app.feature_comments"
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

    implementation(CoreDeps.coreKtx)
    implementation(CoreDeps.appCompat)
    implementation(CoreDeps.material)

    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    DatabaseDeps.allRoomDependencies.forEach { implementation(it) }
    DatabaseDeps.allRoomKaptDependencies.forEach { kapt(it) }

    implementation(platform(FirebaseDeps.firebaseBom)) // Use BOM for version alignment
    implementation(FirebaseDeps.firebaseFirestore)

    implementation(project(ProjectModules.core))
    implementation(project(ProjectModules.uiComponents))

    TestingDeps.TestDependencies.forEach { testImplementation(it) }
    TestingDeps.androidTestDependencies.forEach { androidTestImplementation(it) }

    HiltDeps.allHiltDependencies.forEach { implementation(it) }
    HiltDeps.allHiltKaptDependencies.forEach { kapt(it) }
}