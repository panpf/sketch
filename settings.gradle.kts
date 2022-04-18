include(":sample")
include(":sketch")
include(":sketch-compose")
include(":sketch-extensions")
include(":sketch-gif-movie")
include(":sketch-gif-koral")
include(":sketch-okhttp")
include(":sketch-svg")
include(":sketch-video")
include(":sketch-video-ffmpeg")
include(":sketch-viewability")
include(":sketch-zoom")

enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}