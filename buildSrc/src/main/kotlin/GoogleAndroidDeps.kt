/**
 * Object containing Google Android dependencies and their configurations.
 *
 * This object provides easy access to commonly used Google Play Services and related libraries,
 * encapsulating their group, name, and version information. It also provides a convenient
 * function to retrieve all the defined dependencies as a list of strings suitable for build
 * file configuration.
 */
@Suppress("MemberVisibilityCanBePrivate")
object GoogleAndroidDeps {
    val playServicesMaps = Dependency.VersionedDependency(
        group = "com.google.android.gms",
        name = "play-services-maps",
        version = Versions.playServicesMaps
    ).toGradleNotation

    val playServicesLocation = Dependency.VersionedDependency(
        group = "com.google.android.gms",
        name = "play-services-location",
        version = Versions.playServicesLocation
    ).toGradleNotation

    val playServicesAuth = Dependency.VersionedDependency(
        group = "com.google.android.gms",
        name = "play-services-auth",
        version = Versions.playServicesAuth
    ).toGradleNotation

    val places = Dependency.VersionedDependency(
        group = "com.google.android.libraries.places",
        name = "places",
        version = Versions.googlePlaces
    ).toGradleNotation

    fun getAllPlayServicesDeps() = listOf(
        playServicesMaps,
        playServicesLocation,
        places,
        playServicesAuth
    )
}
