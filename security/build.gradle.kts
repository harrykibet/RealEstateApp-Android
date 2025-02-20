plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.application.real_estate_app.security"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
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

    implementation(SecurityDeps.securityCrypto)

    implementation(GoogleCloudDeps.googleSecretsManager)
    implementation(GoogleCloudDeps.googleCloudKms)

    implementation(project(ProjectModules.core))

    DatabaseDeps.AllRoomDependencies.forEach { implementation(it) }
    DatabaseDeps.AllRoomKaptDependencies.forEach { kapt(it) }

    implementation(SecurityDeps.bouncyCastle)
    implementation(SecurityDeps.bouncyCastlePkix)


    implementation(CachingDeps.caffeine)

    implementation(NetworkDeps.circuit_breaker)
    implementation(NetworkDeps.retry)
    implementation(NetworkDeps.resilience_kotlin)

    implementation(AnalyticsDeps.openTelemetryApi)
    implementation(AnalyticsDeps.openTelemetryExporter)
    implementation(AnalyticsDeps.micrometer)
    implementation(AnalyticsDeps.micrometerPrometheus)
    implementation(AnalyticsDeps.conscrypt)

    TestingDeps.TestDependencies.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDependencies.forEach { androidTestImplementation(it) }

    HiltDeps.AllHiltDependencies.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDependencies.forEach { kapt(it) }
}