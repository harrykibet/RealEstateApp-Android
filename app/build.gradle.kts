plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    // Add the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")
    // Add the Secrets Gradle plugin
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    // Add the Safe Args Gradle plugin
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.kotlin.kapt")
    // Dagger Hilt for Dependency Injection
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.dokka")
    id("org.sonarqube")
}

android {
    namespace = "com.application.real_estate_app"
    compileSdk = 35

    kapt {
        correctErrorTypes = true
        useBuildCache = true
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    defaultConfig {
        applicationId = "com.application.real_estate_app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/COPYRIGHT.txt"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    CoreDeps.CommonCoreDependencies.forEach { implementation(it) }
    CoreDeps.CoreUiDependencies.forEach { implementation(it) }
    implementation(CoreDeps.splashScreen)

    // Lifecycle Dependencies
    LifecycleDeps.AllLifecycleDependencies.forEach { implementation(it) }

    // Firebase Dependencies
    implementation(platform(FirebaseDeps.firebaseBom))
    FirebaseDeps.AllFirebaseDependencies.forEach { implementation(it) }

    // Google Play Services
    GoogleAndroidDeps.AllPlayServicesDependencies.forEach { implementation(it) }

    // EventBus
    implementation(EventBusDeps.eventBus)

    // Security Dependencies
    implementation(SecurityDeps.bouncyCastle)

    // Navigation Dependencies
    NavigationDeps.AllNavigationDependencies.forEach { implementation(it) }

    // Hilt Dependencies
    HiltDeps.AllHiltDependencies.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDependencies.forEach { kapt(it) }

    // Media Libraries
    implementation(MediaDeps.glide)
    kapt(MediaDeps.glideCompiler)

    // Testing Dependencies
    TestingDeps.TestDependencies.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDependencies.forEach { androidTestImplementation(it) }

    // Project Modules
    ProjectModules.AllProjectModules.forEach { implementation(project(it)) }
}


