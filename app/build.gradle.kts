plugins {
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.googleServices)
    id(Plugins.firebaseCrashlytics)
    id(Plugins.secretsPlugin)
    id(Plugins.navigationSafeArgs)
    id(Plugins.kapt)
    id(Plugins.hilt)
    id(Plugins.dokka)
    id(Plugins.sonarQube)
}

android {
    namespace = AndroidConfig.namespace
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
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions.jvmTarget = AndroidConfig.jvmTarget

    packaging {
        resources {
            excludes += Packaging.excludes
        }
    }
}

dependencies {
    CoreDeps.CommonCoreDeps.forEach { implementation(it) }
    CoreDeps.CoreUiDeps.forEach { implementation(it) }
    implementation(CoreDeps.splashScreen)

    // Lifecycle Dependencies
    LifecycleDeps.AllLifecycleDeps.forEach { implementation(it) }

    // Firebase Dependencies
    implementation(platform(FirebaseDeps.firebaseBom))
    FirebaseDeps.AllFirebaseDeps.forEach { implementation(it) }

    // Google Play Services
    GoogleAndroidDeps.AllPlayServicesDeps.forEach { implementation(it) }

    // EventBus
    implementation(EventBusDeps.eventBus)

    // Security Dependencies
    SecurityDeps.BouncyDeps.forEach { implementation(it) }

    // Navigation Dependencies
    NavigationDeps.AllNavigationDeps.forEach { implementation(it) }

    // Hilt Dependencies
    HiltDeps.AllHiltDeps.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDeps.forEach { kapt(it) }
    HiltDeps.AllHiltTestingDeps.forEach { androidTestImplementation(it) }

    // Media Libraries
    MediaDeps.ImageDeps.forEach { implementation(it) }
    MediaDeps.ImageDeps.forEach { kapt(it) }

    // Testing Dependencies
    TestingDeps.TestDeps.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDeps.forEach { androidTestImplementation(it) }

    // Project Modules
    ProjectModules.AllProjectModules.forEach { implementation(project(it)) }
}


