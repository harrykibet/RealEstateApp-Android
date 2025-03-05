plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.kapt)
    id(Plugins.hilt)
    id(Plugins.dokka)
}

android {
    namespace = AndroidConfig.coreNamespace
    compileSdk = AndroidConfig.compileSdk

    kapt {
        correctErrorTypes = true
        useBuildCache = true
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir(AndroidConfig.dokkaPath))
    }

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        testInstrumentationRunner = AndroidConfig.testInstrumentationRunner
        consumerProguardFiles(AndroidConfig.proguardConsumerRulesFile)
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
        resources {
            excludes += Packaging.excludes
        }
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

    implementation(platform(FirebaseDeps.firebaseBom))
    FirebaseDeps.AllFirebaseDeps.forEach { implementation(it) }

    implementation(MediaDeps.media3ExoPlayer)
    implementation(MediaDeps.media3UI)
    implementation(MediaDeps.ffmpeg)
    MediaDeps.ImageDeps.forEach { implementation(it) }
    MediaDeps.ImageKaptDeps.forEach { kapt(it) }

    NetworkDeps.AllNetworkDeps.forEach { implementation(it) }

    TestingDeps.TestDeps.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDeps.forEach { androidTestImplementation(it) }

    HiltDeps.AllHiltDeps.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDeps.forEach { kapt(it) }
}
