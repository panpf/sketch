plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.test.view")

dependencies {
    api(projects.internal.test)
    api(projects.sketchViewCore)
}