
@file:Suppress("ConstPropertyName")
object FirebaseDeps {
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.google_firebase_bom}"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
    const val firebaseFirestore = "com.google.firebase:firebase-firestore-ktx"
    const val firebaseStorage = "com.google.firebase:firebase-storage-ktx"
    const val playIntergrity = "com.google.firebase:firebase-appcheck-playintegrity"
    const val appCheckDebug = "com.google.firebase:firebase-appcheck-debug"

    // FirebaseBom not included, add it manually in the dependencies block
    val allFirebaseDependencies = listOf(
        firebaseAnalytics,
        firebaseCrashlytics,
        firebaseAuth,
        firebaseFirestore,
        firebaseStorage,
        playIntergrity,
        appCheckDebug
    )
}