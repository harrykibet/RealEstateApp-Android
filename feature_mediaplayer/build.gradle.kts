plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.kapt)
    id(Plugins.hilt)
    id(Plugins.dokka)
}

android {
    namespace = AndroidConfig.featureMediaPlayerNamespace
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
}

dependencies {
    MediaDeps.AllMedia3Deps.forEach { implementation(it) }

    implementation(project(ProjectModules.core))

    implementation(platform(FirebaseDeps.firebaseBom))
    implementation(FirebaseDeps.firebaseAnalytics)

    DatabaseDeps.AllRoomDeps.forEach { implementation(it) }
    DatabaseDeps.AllRoomKaptDeps.forEach { kapt(it) }

    TestingDeps.TestDeps.forEach { testImplementation(it) }
    TestingDeps.AndroidTestDeps.forEach { androidTestImplementation(it) }

    HiltDeps.AllHiltDeps.forEach { implementation(it) }
    HiltDeps.AllHiltKaptDeps.forEach { kapt(it) }
}
