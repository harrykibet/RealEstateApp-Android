
@file:Suppress("ConstPropertyName")
object FirebaseDeps {
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics"
    const val firebaseAuth = "com.google.firebase:firebase-auth"
    const val firebaseFirestore = "com.google.firebase:firebase-firestore"
    const val firebaseStorage = "com.google.firebase:firebase-storage"
    const val firebasePerformance = "com.google.firebase:firebase-perf"
    const val firebaseConfig = "com.google.firebase:firebase-config"
    const val playIntergrity = "com.google.firebase:firebase-appcheck-playintegrity"
    const val appCheckDebug = "com.google.firebase:firebase-appcheck-debug"

    // FirebaseBom not included, add it manually in the dependencies block
    val AllFirebaseDeps = listOf(
        firebaseAnalytics,
        firebasePerformance,
        firebaseConfig,
        firebaseCrashlytics,
        firebaseAuth,
        firebaseFirestore,
        firebaseStorage,
        playIntergrity,
        appCheckDebug
    )
}