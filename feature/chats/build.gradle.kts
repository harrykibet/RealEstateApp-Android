plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.chats"
}

dependencies {
    implementation(libs.kotlinx.datetime)
}
