/**
 * Object containing dependencies related to the Room persistence library.
 *
 * This object provides convenient access to commonly used Room dependencies,
 * such as the Room KTX, runtime, and compiler. It also provides helper
 * functions to retrieve these dependencies in a format suitable for build
 * scripts.
 */
@Suppress("MemberVisibilityCanBePrivate")
object DatabaseDeps {
    val roomKtx = Dependency.VersionedDependency(
        group = "androidx.room",
        name = "room-ktx",
        version = Versions.room
    )

    val roomRuntime = Dependency.VersionedDependency(
        group = "androidx.room",
        name = "room-runtime",
        version = Versions.room
    )

    val roomCompiler = Dependency.VersionedDependency(
        group = "androidx.room",
        name = "room-compiler",
        version = Versions.room
    )


    fun getRoomDeps() = listOf(
        roomKtx,
        roomRuntime
    ).map { it.get() }

    fun getRoomKaptDeps() = listOf(
        roomCompiler
    ).map { it.get() }
}
