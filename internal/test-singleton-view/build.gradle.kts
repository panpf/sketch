plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.test.singleton.view")

dependencies {
    api(projects.sketchViewCore)
    api(projects.internal.testSingleton)
}