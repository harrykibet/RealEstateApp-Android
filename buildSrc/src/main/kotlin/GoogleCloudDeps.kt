/**
 * A collection of dependencies related to Google Cloud Platform services.
 *
 * This object provides access to the Google Cloud Libraries Bill of Materials (BOM)
 * and a set of commonly used Google Cloud dependencies that are managed by the BOM.
 * Using the BOM ensures that all Google Cloud dependencies are compatible with each other.
 *
 * @suppress MemberVisibilityCanBePrivate - This object is designed for public use in build scripts.
 */
@Suppress("MemberVisibilityCanBePrivate")
object GoogleCloudDeps {
    val googleCloudBom = Dependency.BomDependency(
        group = "com.google.cloud",
        name = "libraries-bom",
        version = Versions.googleCloudBom
    ).toGradleNotation

    val googleSecretsManager = Dependency.BomManagedDependency(
        group = "com.google.cloud",
        name = "google-cloud-secretmanager"
    ).toGradleNotation

    val googleAuthCloud = Dependency.BomManagedDependency(
        group = "com.google.auth",
        name = "google-auth-library-oauth2-http"
    ).toGradleNotation

    val googleCloudKms = Dependency.BomManagedDependency(
        group = "com.google.cloud",
        name = "google-cloud-kms"
    ).toGradleNotation

    
    fun getAllManagedDependencies() = listOf(
        googleSecretsManager,
        googleAuthCloud,
        googleCloudKms
    )
}
