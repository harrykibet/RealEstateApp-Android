/**
 * Represents a dependency in a build configuration.
 *
 * This sealed class provides different types of dependencies:
 * - [VersionedDependency]: A dependency with a specific group, name, and version.
 * - [BomDependency]: A Bill of Materials (BOM) dependency, specifying a group, name, and version.
 * - [BomManagedDependency]: A dependency managed by a BOM, specifying only the group and name.
 */
sealed class Dependency {


    /**
     * Retrieves a string representation of the object.
     *
     * This is an abstract function that must be implemented by all concrete subclasses.
     * It provides a way to get a string value associated with an instance of the class.
     * The specific meaning and content of the returned string are determined by the
     * subclass's implementation.
     *
     * @return A String representing the object.
     */
    abstract val toGradleNotation: String

    /**
     * Represents a dependency with a specific group, name, and version.
     *
     * This class encapsulates the information needed to uniquely identify a library or module
     * within a project's dependency graph, including its group ID, artifact name, and version.
     *
     * @property group The group ID of the dependency (e.g., "com.example").
     * @property name The name of the dependency artifact (e.g., "mylibrary").
     * @property version The version of the dependency (e.g., "1.0.0").
     */
    data class VersionedDependency(val group: String, val name: String, val version: String) : Dependency() {
        override val toGradleNotation get() = "$group:$name:$version"
    }

    /**
     * Represents a Bill of Materials (BOM) dependency.
     *
     * A BOM is a special type of dependency that is used to manage the versions of other dependencies.
     * It does not directly bring in any code itself, but rather acts as a centralized place to define
     * compatible versions of libraries. This ensures consistency and avoids conflicts between transitive
     * dependencies.
     *
     * @property group The group ID of the BOM dependency (e.g., "com.google.cloud").
     * @property name The artifact ID of the BOM dependency (e.g., "libraries-bom").
     * @property version The version of the BOM dependency (e.g., "26.32.0").
     */
    data class BomDependency(val group: String, val name: String, val version: String) : Dependency() {
        override val toGradleNotation get() = "$group:$name:$version"
    }

    /**
     * Represents a managed dependency defined in a Bill of Materials (BOM).
     *
     * This class encapsulates the group ID and artifact ID of a dependency that is managed
     * by a BOM.  A BOM provides a consistent set of dependency versions, and including
     * a managed dependency in your project will automatically resolve its version from the BOM.
     *
     * @property group The group ID of the managed dependency (e.g., "com.example").
     * @property name The artifact ID of the managed dependency (e.g., "my-library").
     *
     * @constructor Creates a new instance of BomManagedDependency.
     *
     * @see Dependency
     */
    data class BomManagedDependency(val group: String, val name: String) : Dependency() {
        override val toGradleNotation get() = "$group:$name"
    }
}
