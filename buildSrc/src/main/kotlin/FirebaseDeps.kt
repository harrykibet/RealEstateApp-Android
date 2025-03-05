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
    )

    val firebaseAnalytics = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-analytics"
    )

    val firebaseCrashlytics = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-crashlytics"
    )

    val firebaseAuth = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-auth"
    )

    val firebaseFirestore = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-firestore"
    )

    val firebaseStorage = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-storage"
    )

    val firebasePerformance = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-perf"
    )

    val firebaseConfig = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-config"
    )

    val playIntegrity = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-appcheck-playintegrity"
    )

    val appCheckDebug = Dependency.BomManagedDependency(
        group = "com.google.firebase",
        name = "firebase-appcheck-debug"
    )

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
    ).map { it.get() }

    fun getFirebaseBom() = firebaseBom.get()
}
