plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.view")

dependencies {
    api(projects.sketchViewCore)
    api(projects.sketchSingleton)
}