plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.realestateapp.android.room)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
    alias(libs.plugins.org.jetbrains.dokka)
}

android {
    namespace = "com.application.real_estate_app.feature_profile"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }

    buildFeatures {
        viewBinding = true
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
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(platform(libs.compose.bom))
    implementation(libs.material3)

    implementation(libs.firebase.auth)

    implementation(libs.bundles.navigation)

    androidTestImplementation(libs.bundles.androidTesting)
    testImplementation(libs.bundles.testing)

    implementation(libs.eventbus)

    implementation(projects.uiComponents)
    implementation(projects.core)
}
