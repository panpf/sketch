plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.video")

dependencies {
    api(projects.sketchCore)
    androidTestImplementation(projects.internal.testUtils)
}