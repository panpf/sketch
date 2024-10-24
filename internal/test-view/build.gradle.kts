plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.test.view")

dependencies {
    api(projects.internal.test)
    api(projects.sketchViewCore)
}