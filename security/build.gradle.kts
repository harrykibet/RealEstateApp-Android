plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.kapt)
    id(Plugins.hilt)
    id(Plugins.dokka)
}

android {
    namespace = AndroidConfig.securityNamespace
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        testInstrumentationRunner = AndroidConfig.testInstrumentationRunner
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

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(AndroidConfig.jvmTarget.toInt()))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = AndroidConfig.jvmTarget
    }

    packaging {
        resources.excludes.addAll(Packaging.excludes)
    }
}

dependencies {
    implementation(CoreDeps.coreKtx)
    implementation(CoreDeps.appCompat)
    implementation(CoreDeps.material)

    implementation(SecurityDeps.securityCrypto)

    // Google Cloud Dependencies (KMS & Secret Manager)
    implementation(platform(GoogleCloudDeps.googleCloudBom)) // Version alignment for Google Cloud libraries
    implementation(GoogleCloudDeps.googleCloudKms)
    implementation(GoogleCloudDeps.googleSecretsManager)

    implementation(project(ProjectModules.core))

    DatabaseDeps.AllRoomDeps.forEach { implementation(it) }
    DatabaseDeps.AllRoomKaptDeps.forEach { kapt(it) }

    implementation(SecurityDeps.bouncyCastle)
    implementation(SecurityDeps.bouncyCastlePkix)

    implementation(CachingDeps.caffeine)

    // Observability & Monitoring
    implementation(AnalyticsDeps.openTelemetryApi)
    implementation(AnalyticsDeps.openTelemetryExporter)
    implementation(AnalyticsDeps.micrometer)
    implementation(AnalyticsDeps.micrometerPrometheus)
    implementation(AnalyticsDeps.conscrypt)

    TestingDeps.TestDeps.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDeps.forEach { androidTestImplementation(it) }

    HiltDeps.AllHiltDeps.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDeps.forEach { kapt(it) }
}
