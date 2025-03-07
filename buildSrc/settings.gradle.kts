rootProject.name="buildSrc"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files(rootDir.resolve("../gradle/libs.versions.toml")))
        }
    }
}
