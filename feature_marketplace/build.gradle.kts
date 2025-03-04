plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id ("com.google.dagger.hilt.android")
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.application.real_estate_app.feature_marketplace"
    compileSdk = 35

    kapt{
        correctErrorTypes = true
        useBuildCache = true
    }

    tasks.dokkaHtml.configure {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }

    buildFeatures{
        viewBinding = true
        dataBinding = true
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

    DatabaseDeps.AllRoomDeps.forEach { implementation(it) }
    DatabaseDeps.AllRoomKaptDeps.forEach { kapt(it) }

    implementation(project(ProjectModules.uiComponents))
    implementation(project(ProjectModules.core))

    TestingDeps.TestDeps.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDeps.forEach { androidTestImplementation(it) }

    HiltDeps.AllHiltDeps.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDeps.forEach { kapt(it) }
}