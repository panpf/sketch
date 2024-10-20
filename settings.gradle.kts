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
include(":sketch-animated-core")
include(":sketch-animated-gif")
include(":sketch-animated-gif-koral")
include(":sketch-animated-heif")
include(":sketch-animated-webp")
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
include(":sketch-http-hurl")
include(":sketch-http-ktor2")
include(":sketch-http-ktor3")
include(":sketch-http-okhttp")
include(":sketch-singleton")
include(":sketch-svg")
include(":sketch-video")
include(":sketch-video-core")
include(":sketch-video-ffmpeg")
include(":sketch-view")
include(":sketch-view-core")

/*
 * Private
 */
include(":sample")
include(":internal:images")
include(":internal:test")
include(":internal:test-compose")
include(":internal:test-singleton")
