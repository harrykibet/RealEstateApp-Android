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
    CoreDeps.getCommonCoreDeps().forEach { implementation(it) }
    CoreDeps.getCoreUiDeps().forEach { implementation(it) }
    implementation(CoreDeps.splashScreen)

    // Lifecycle Dependencies
    LifecycleDeps.getAllLifecycleDeps().forEach { implementation(it) }

    // Firebase Dependencies
    implementation(platform(FirebaseDeps.getFirebaseBom()))
    FirebaseDeps.getAllFirebaseDeps().forEach { implementation(it) }

    // Google Play Services
    GoogleAndroidDeps.getAllPlayServicesDeps().forEach { implementation(it) }

    // EventBus
    implementation(EventBusDeps.eventBus)

    // Security Dependencies
    SecurityDeps.getBouncyDeps().forEach { implementation(it) }

    // Navigation Dependencies
    NavigationDeps.getAllNavigationDeps().forEach { implementation(it) }

    // Hilt Dependencies
    HiltDeps.getAllHiltDeps().forEach { implementation(it) }
    HiltDeps.getAllHiltKaptDeps().forEach { kapt(it) }
    HiltDeps.getAllHiltTestingDeps().forEach { androidTestImplementation(it) }

    // Media Libraries
    MediaDeps.getImageDeps().forEach { implementation(it) }
    MediaDeps.getImageKaptDeps().forEach { kapt(it) }

    // Testing Dependencies
    TestingDeps.getTestDeps().forEach { testImplementation(it) }
    TestingDeps.getAndroidTestDeps().forEach { androidTestImplementation(it) }

    // Project Modules
    ProjectModules.AllProjectModules.forEach { implementation(project(it)) }
}


