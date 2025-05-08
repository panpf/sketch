rootProject.name = "buildSrc" // https://github.com/gradle/gradle/issues/30299

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
        }
    }
}
