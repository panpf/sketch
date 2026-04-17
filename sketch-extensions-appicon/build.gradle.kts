plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.appicon")

dependencies {
    api(projects.sketchCore)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testSingleton)
}