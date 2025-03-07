plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.dokka)
}

android {
    namespace = AndroidConfig.featurePropertyNamespace
    compileSdk = AndroidConfig.compileSdk

    kapt {
        correctErrorTypes = true
        useBuildCache = true
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir(AndroidConfig.dokkaPath))
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
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
    implementation(libs.material)

    implementation(libs.navigation.fragment.ktx)

    implementation(libs.gson)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)

    // Glide Dependencies
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(project(ProjectModules.core))
    implementation(project(ProjectModules.uiComponents))

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    implementation(libs.bundles.lifecycle)

    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)

    androidTestImplementation(libs.bundles.androidTesting)
    testImplementation(libs.bundles.testing)

    // Hilt Dependencies
    implementation(libs.bundles.hilt)
    kapt(libs.bundles.hiltKapt)
}
