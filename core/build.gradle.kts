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
    implementation(CoreDeps.metrics)
    implementation(CoreDeps.appSet)
    implementation(CoreDeps.guava)
    implementation(CoreDeps.workRuntimeKtx)

    implementation(platform(FirebaseDeps.firebaseBom)) // Firebase BOM for managing versions
    FirebaseDeps.allFirebaseDependencies.forEach { implementation(it) }

    MediaDeps.allMedia3Dependencies.forEach {implementation(it) }
    implementation(MediaDeps.ffmpeg)
    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    NetworkDeps.allNetworkDependencies.forEach {implementation(it) }

    TestingDeps.TestDependencies.forEach { testImplementation(it) }
    TestingDeps.androidTestDependencies.forEach { androidTestImplementation(it) }

    HiltDeps.allHiltDependencies.forEach { implementation(it) }
    HiltDeps.allHiltKaptDependencies.forEach { kapt(it) }
}
