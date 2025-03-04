plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.application.real_estate_app.feature_auth"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }

    kapt{
        correctErrorTypes = true
        useBuildCache = true
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    implementation(CoreDeps.coreKtx)
    implementation(CoreDeps.appCompat)
    implementation(CoreDeps.material)

    implementation(platform(FirebaseDeps.firebaseBom)) // Using Firebase BOM for consistent versioning
    implementation(FirebaseDeps.firebaseAuth)
    implementation(FirebaseDeps.firebaseFirestore)

    implementation(GoogleAndroidDeps.playServicesAuth)

    DatabaseDeps.AllRoomDeps.forEach { implementation(it) }
    DatabaseDeps.AllRoomKaptDeps.forEach { kapt(it) }

    NavigationDeps.AllNavigationDeps.forEach{ implementation(it) }

    implementation(project(ProjectModules.uiComponents))
    implementation(project(ProjectModules.core))

    implementation(EventBusDeps.eventBus)

    TestingDeps.TestDeps.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDeps.forEach { androidTestImplementation(it) }

    HiltDeps.AllHiltDeps.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDeps.forEach { kapt(it) }
}
