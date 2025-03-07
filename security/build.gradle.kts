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
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.security.crypto.ktx)

    // Google Cloud Dependencies (KMS & Secret Manager)
    implementation(platform(libs.google.cloud.bom)) // Version alignment for Google Cloud libraries
    implementation(libs.google.cloud.kms)
    implementation(libs.google.cloud.secretmanager)

    implementation(project(ProjectModules.core))

    implementation(libs.bundles.bouncy)

    implementation(libs.caffeine)

    implementation(libs.bundles.analytics)

    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)

    androidTestImplementation(libs.bundles.androidTesting)
    testImplementation(libs.bundles.testing)

    // Hilt Dependencies
    implementation(libs.bundles.hilt)
    kapt(libs.bundles.hiltKapt)
}
