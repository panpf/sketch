rootProject.name = "sketch"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

/*
 * Release
 */
include(":sketch-animated")
include(":sketch-animated-koralgif")
include(":sketch-compose")
include(":sketch-compose-core")
include(":sketch-compose-resources")
include(":sketch-core")
include(":sketch-extensions-core")
include(":sketch-extensions-compose")
include(":sketch-extensions-compose-resources")
include(":sketch-extensions-view")
include(":sketch-extensions-viewability")
include(":sketch-http-core")
include(":sketch-http-ktor")
include(":sketch-http-okhttp")
include(":sketch-singleton")
include(":sketch-svg")
include(":sketch-video")
include(":sketch-video-ffmpeg")
include(":sketch-view")
include(":sketch-view-core")

/*
 * Private
 */
include(":sample")
include(":internal:images")
include(":internal:test-utils")
include(":internal:test-utils-core")
include(":internal:test-utils-compose")
include(":internal:test-utils-compose-core")