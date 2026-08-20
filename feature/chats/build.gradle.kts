plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.chats"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.designSystem)
    implementation(projects.core.navigation)
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.compose)
    implementation(libs.kotlinx.datetime)
    
    implementation(libs.coil.compose)
}
