plugins {
    id("com.android.application")
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.com.google.firebase.crashlytics)
    alias(libs.plugins.com.google.gms.google.services)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.dokka)
    alias(libs.plugins.org.sonarqube)
}

android {
    namespace = AndroidConfig.appNamespace
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        applicationId = AndroidConfig.applicationId
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk
        versionCode = AndroidConfig.versionCode
        versionName = AndroidConfig.versionName

        testInstrumentationRunner = AndroidConfig.testInstrumentationRunner
        vectorDrawables.useSupportLibrary = true
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir(AndroidConfig.dokkaPath))
    }

    kotlinOptions.jvmTarget = AndroidConfig.jvmTarget

    packaging {
        resources {
            excludes += Packaging.excludes
        }
    }
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.core.splashscreen)

    // Lifecycle Dependencies
    implementation(libs.bundles.lifecycle)

    // Firebase Dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Google Play Services
    implementation(libs.bundles.play)

    // EventBus
    implementation(libs.eventbus)

    // Security Dependencies
    implementation(libs.bundles.bouncy)

    // Navigation Dependencies
    implementation(libs.bundles.navigation)

    // Hilt Dependencies
    androidTestImplementation(libs.hilt.android.testing)
    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hiltKapt)

    // Glide Dependencies
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    // Testing Dependencies
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.androidTesting)

    // Project Modules
    ProjectModules.AllProjectModules.forEach { implementation(project(it)) }
}


