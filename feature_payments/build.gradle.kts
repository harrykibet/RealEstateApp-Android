plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.kapt)
    id(Plugins.hilt)
    id(Plugins.dokka)
}

android {
    namespace = AndroidConfig.featurePaymentsNamespace
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
    CoreDeps.getCommonCoreDeps().forEach { implementation(it) }
    implementation(CoreDeps.material)

    LifecycleDeps.getAllLifecycleDeps().forEach { implementation(it) }

    implementation(project(ProjectModules.uiComponents))
    implementation(project(ProjectModules.core))

    DatabaseDeps.getRoomDeps().forEach { implementation(it) }
    DatabaseDeps.getRoomKaptDeps().forEach { kapt(it) }

    TestingDeps.getTestDeps().forEach { testImplementation(it) }
    TestingDeps.getAndroidTestDeps().forEach { androidTestImplementation(it) }

    HiltDeps.getAllHiltDeps().forEach { implementation(it) }
    HiltDeps.getAllHiltKaptDeps().forEach { kapt(it) }
}
