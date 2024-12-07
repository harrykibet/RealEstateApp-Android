// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories{
        google()
        mavenCentral()
    }
    dependencies{
        classpath("com.android.tools.build:gradle:8.6.1")
        classpath("com.google.gms:google-services:4.4.2") //for firebase
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.3") // Safe Args plugin
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52") // Dagger hilt for dependency injection
        classpath("androidx.room:room-gradle-plugin:2.6.1")
    }
}
plugins {
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    //the dependency for google services gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false
    // Add the dependency for the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.android.library") version "8.6.1" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.10" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.24" apply false
}