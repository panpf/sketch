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
 * Public Modules
 *
 * Synced with buildSrc/src/main/kotlin/utils.kt publicModules list.
 */
include(":sketch-animated-core")
include(":sketch-animated-gif")
include(":sketch-animated-gif-koral")
include(":sketch-animated-heif")
include(":sketch-animated-webp")
include(":sketch-blurhash")
include(":sketch-compose")
include(":sketch-compose-core")
include(":sketch-compose-koin")
include(":sketch-compose-resources")
include(":sketch-core")
include(":sketch-extensions-appicon")
include(":sketch-extensions-apkicon")
include(":sketch-extensions-core")
include(":sketch-extensions-compose")
include(":sketch-extensions-compose-resources")
include(":sketch-extensions-view")
include(":sketch-extensions-viewability")
include(":sketch-http")
include(":sketch-http-core")
include(":sketch-http-hurl")
include(":sketch-http-ktor2")
include(":sketch-http-ktor2-core")
include(":sketch-http-ktor3")
include(":sketch-http-ktor3-core")
include(":sketch-http-okhttp")
include(":sketch-koin")
include(":sketch-singleton")
include(":sketch-svg")
include(":sketch-video")
include(":sketch-video-core")
include(":sketch-video-ffmpeg")
include(":sketch-view")
include(":sketch-view-core")
include(":sketch-view-koin")

/*
 * Private
 */
include(":sample")
include(":internal:componentLoaderTest")
include(":internal:images")
include(":internal:test")
include(":internal:test-compose")
include(":internal:test-koin")
include(":internal:test-http")
include(":internal:test-singleton")
include(":internal:test-singleton-view")
include(":internal:test-view")
