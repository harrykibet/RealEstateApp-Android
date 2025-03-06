/**
 * Object containing Firebase dependencies and helper functions for managing them.
 *
 * This object centralizes the definition of Firebase dependencies used in the project.
 * It provides both the Bill of Materials (BOM) dependency and individual Firebase
 * library dependencies managed by the BOM.
 *
 * The object also provides helper functions to retrieve lists of all Firebase
 * dependencies or specifically the BOM dependency, simplifying dependency management
 * within Gradle build scripts.
 */
@Suppress("MemberVisibilityCanBePrivate")
object FirebaseDeps {
    val firebaseBom = Dependency.BomDependency(
        group = "com.google.firebase",
        name = "firebase-bom",
        version = Versions.firebaseBom
    ).toGradleNotation

    val firebaseAnalytics = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-analytics"
    ).toGradleNotation

    val firebaseCrashlytics = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-crashlytics"
    ).toGradleNotation

    val firebaseAuth = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-auth"
    ).toGradleNotation

    val firebaseFirestore = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-firestore"
    ).toGradleNotation

    val firebaseStorage = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-storage"
    ).toGradleNotation

    val firebasePerformance = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-perf"
    ).toGradleNotation

    val firebaseConfig = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-config"
    ).toGradleNotation

    val playIntegrity = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-appcheck-playintegrity"
    ).toGradleNotation

    val appCheckDebug = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-appcheck-debug"
    ).toGradleNotation

    fun getAllFirebaseDeps() = listOf(
        firebaseAnalytics,
        firebasePerformance,
        firebaseConfig,
        firebaseCrashlytics,
        firebaseAuth,
        firebaseFirestore,
        firebaseStorage,
        playIntegrity,
        appCheckDebug
    )
}
