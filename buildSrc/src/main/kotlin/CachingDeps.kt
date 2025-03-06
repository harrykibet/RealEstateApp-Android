/**
 * Object containing dependencies related to caching.
 *
 * This object provides a centralized location to manage and access caching-related
 * dependencies used within the project. It defines specific dependencies, such as Caffeine,
 * and offers a utility function to retrieve all configured caching dependencies.
 */
@Suppress("MemberVisibilityCanBePrivate")
object CachingDeps {
    val caffeine = Dependency.VersionedDependency(
        group = "com.github.ben-manes.caffeine",
        name = "caffeine",
        version = Versions.caffeine
    ).toGradleNotation

    fun getAllCachingDeps() = listOf(
        caffeine
    )
}
