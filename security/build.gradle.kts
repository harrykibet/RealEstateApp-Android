plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.dokka)
}

android {
    namespace = AndroidConfig.securityNamespace
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        testInstrumentationRunner = AndroidConfig.testInstrumentationRunner
        consumerProguardFiles(AndroidConfig.proguardConsumerRulesFile)
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir(AndroidConfig.dokkaPath))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(AndroidConfig.proguardOptimizationFile),
                AndroidConfig.proguardRulesFile
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

    implementation(SecurityDeps.bouncyCastle)
    implementation(SecurityDeps.bouncyCastlePkix)

    implementation(CachingDeps.caffeine)

    // Observability & Monitoring
    implementation(AnalyticsDeps.openTelemetryApi)
    implementation(AnalyticsDeps.openTelemetryExporter)
    implementation(AnalyticsDeps.micrometer)
    implementation(AnalyticsDeps.micrometerPrometheus)
    implementation(AnalyticsDeps.conscrypt)

    DatabaseDeps.getRoomDeps().forEach { implementation(it) }
    DatabaseDeps.getRoomKaptDeps().forEach { kapt(it) }

    TestingDeps.getTestDeps().forEach { testImplementation(it) }
    TestingDeps.getAndroidTestDeps().forEach { androidTestImplementation(it) }

    HiltDeps.getAllHiltDeps().forEach { implementation(it) }
    HiltDeps.getAllHiltKaptDeps().forEach { kapt(it) }
}
