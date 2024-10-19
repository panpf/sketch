plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.video.ffmpeg")

dependencies {
    api(projects.sketchVideoCore)
    api(libs.ffmpegMediaMetadataRetriever.core)
    api(libs.ffmpegMediaMetadataRetriever.native)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testSingleton)
}