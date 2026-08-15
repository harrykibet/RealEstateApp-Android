# Add project-specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Administrator\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools-proguard.html

# Add any project specific keep rules here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-keepattributes SourceFile,LineNumberTable

# Fix R8 Missing Class Errors
# These classes are referenced by dependencies but are not used or provided in the Android environment.

# Firestore (referenced due to exclusions of protolite-well-known-types)
-dontwarn com.google.rpc.Status
-dontwarn com.google.type.LatLng
-dontwarn com.google.type.LatLng$Builder

# Micrometer
-dontwarn io.micrometer.context.ThreadLocalAccessor
-dontwarn org.LatencyUtils.**

# OpenTelemetry (Auto-configuration SPIs not needed for basic Android instrumentation)
-dontwarn io.opentelemetry.sdk.autoconfigure.spi.**

# SLF4J (Referenced by Micrometer logging bridge)
-dontwarn org.slf4j.**

# BouncyCastle
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# Hilt Worker
-keep class * extends androidx.work.ListenableWorker {
    <init>(android.content.Context, androidx.work.WorkerParameters);
}
