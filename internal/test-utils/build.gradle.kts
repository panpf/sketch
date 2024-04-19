plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.test.singleton")

dependencies {
    api(projects.sketchCore)
    api(projects.sketchViewCore)
    api(projects.internal.testUtilsCore)
}