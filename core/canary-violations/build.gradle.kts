plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.android.compose)
}

android {
    namespace = "com.estatia.realestate.apps.core.canary_violations"
    
    lint {
        abortOnError = false
        checkDependencies = false
        checkTestSources = true
    }
}

dependencies {
    lintChecks(projects.lint)
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.feature.auth)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.lifecycle.viewmodel.ktx)
}
