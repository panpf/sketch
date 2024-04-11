plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.video.ffmpeg")

dependencies {
    api(projects.sketchCore)
    api(projects.sketchVideo)
    api(libs.ffmpegMediaMetadataRetriever.core)
    api(libs.ffmpegMediaMetadataRetriever.native)
    androidTestImplementation(projects.internal.testUtils)
}