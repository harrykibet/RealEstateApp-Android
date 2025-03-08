plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.dokka)
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
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.test.core)
    implementation(libs.core.testing)
    implementation(libs.work.runtime.ktx)

    implementation(libs.metrics.performance)
    implementation(libs.play.services.appset)
    implementation(libs.guava)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.ffmpeg.kit.min.gpl)

    // Glide Dependencies
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(libs.bundles.networking)

    androidTestImplementation(libs.bundles.androidTesting)
    testImplementation(libs.bundles.testing)

    // Hilt Dependencies
    implementation(libs.bundles.hilt)
    kapt(libs.bundles.hiltKapt)
}
