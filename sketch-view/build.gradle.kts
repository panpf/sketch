plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.view")

dependencies {
    api(projects.sketchViewCore)
    api(projects.sketchSingleton)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testView)
}