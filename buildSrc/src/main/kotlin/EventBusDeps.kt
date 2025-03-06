/**
 * `EventBusDeps` is an object that encapsulates the dependencies related to the EventBus library.
 *
 * It provides a convenient way to manage and access the EventBus dependency and its resolved
 * dependencies for use in dependency injection or other dependency management scenarios.
 */
@Suppress("MemberVisibilityCanBePrivate")
object EventBusDeps {
    val eventBus = Dependency.VersionedDependency(
        group = "org.greenrobot",
        name = "eventbus",
        version = Versions.eventBus
    ).toGradleNotation

    val getEventBusDeps = listOf(
        eventBus
    )
}
