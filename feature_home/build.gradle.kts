plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.dokka)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    namespace = AndroidConfig.featureHomeNamespace
    compileSdk = AndroidConfig.compileSdk


    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir(AndroidConfig.dokkaPath))
    }

    buildFeatures {
        viewBinding = true
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
}

dependencies {

    implementation(libs.bundles.android)

    implementation(platform(libs.compose.bom))
    implementation(libs.material3)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.exoplayer.hls)

    // Glide Dependencies
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)

    implementation(project(ProjectModules.core))
    implementation(project(ProjectModules.uiComponents))

    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    implementation(libs.bundles.navigation)

    implementation(libs.bundles.lifecycle)

    androidTestImplementation(libs.bundles.androidTesting)
    testImplementation(libs.bundles.testing)

    // Hilt Dependencies
    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hiltKapt)
}
