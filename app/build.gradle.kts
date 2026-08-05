 plugins {
    alias(libs.plugins.estatia.android.application)
}

android {
    namespace = "com.estatia.realestate.apps"
}

 dokka {
     moduleName.set("RealEstateApp")
     dokkaSourceSets {
         configureEach {
             suppress.set(true)
             skipEmptyPackages.set(true)
             reportUndocumented.set(false)
         }
     }
 }

 hilt {
     enableAggregatingTask = true
 }

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.core.splashscreen)

    implementation(libs.bundles.lifecycle)

    implementation(libs.kotlinx.datetime)

    implementation(libs.kotlinx.coroutines.guava)

    implementation(libs.metrics.performance)

    implementation(libs.firebase.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.appcheck.playintegrity)

    implementation(libs.bundles.play)

    implementation(libs.eventbus)

    implementation(libs.bundles.bouncy)

    implementation(libs.bundles.navigation)
    implementation(libs.androidx.profileinstaller)

    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.navigationSuite)
    implementation(libs.androidx.compose.material3.windowSizeClass)

    androidTestImplementation(libs.hilt.android.testing)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.config)
    implementation(projects.core.network)
    implementation(projects.core.testing)
    implementation(projects.core.playerUi)
    implementation(projects.core.security)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.analytics)
    implementation(projects.core.navigation)
    implementation(projects.core.localization)
    implementation(projects.core.playerEngine)
    implementation(projects.core.designSystem)
    implementation(projects.core.notifications)
    implementation(projects.core.datastoreProto)


    implementation(projects.feature.auth)
    implementation(projects.feature.home)
    implementation(projects.feature.chats)
    implementation(projects.feature.market)
    implementation(projects.feature.search)
    implementation(projects.feature.service)
    implementation(projects.feature.profile)
    implementation(projects.feature.comments)
    implementation(projects.feature.property)
    implementation(projects.feature.settings)
    implementation(projects.feature.payments)
    implementation(projects.feature.favorites)
    implementation(projects.feature.intelligence)

    implementation(projects.lint)
}


