plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.android.compose)
}

// 🛡️ High-Fidelity Oracle: Disable KSP for the canary module 
// to prevent deliberate violations from breaking the build 
// before Lint can report them.
tasks.configureEach {
    if (name.contains("ksp", ignoreCase = true)) {
        enabled = false
    }
}

android {
    namespace = "com.estatia.realestate.apps.core.canary_violations"
    
    lint {
        // 🏗️ Canary Policy: abortOnError is false because we WANT to produce a report with violations.
        abortOnError = false
        
        // 🧪 Regression Enforcement: Structured reports for high-fidelity verification (Law034).
        textReport = true
        textOutput = file("build/reports/lint-results.txt")
        xmlReport = true
        xmlOutput = file("build/reports/lint-results.xml")
        
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
    implementation(libs.mockkAndroid)
    implementation(libs.retrofit)
}
