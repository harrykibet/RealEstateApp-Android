plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.real_estate_app.core"
    compileSdk = 35

    kapt{
        correctErrorTypes = true
        useBuildCache = true
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
    implementation(CoreDeps.testCoreKtx)
    implementation(CoreDeps.workRuntimeKtx)

    implementation(AnalyticsDeps.androidMetrics)
    implementation(AnalyticsDeps.appSet)
    implementation(AnalyticsDeps.guava)

    implementation(platform(FirebaseDeps.firebaseBom)) // Firebase BOM for managing versions
    FirebaseDeps.AllFirebaseDependencies.forEach { implementation(it) }

    implementation(MediaDeps.media3ExoPlayer)
    implementation(MediaDeps.media3UI)
    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    implementation(GoogleDeps.googleAuthCloud)
    implementation(GoogleDeps.googleSecretsManager)

    NetworkDeps.AllNetworkDependencies.forEach {implementation(it) }

    TestingDeps.TestDependencies.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDependencies.forEach { androidTestImplementation(it) }

    HiltDeps.AllHiltDependencies.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDependencies.forEach { kapt(it) }
}
