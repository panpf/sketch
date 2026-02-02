plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.view.core")

dependencies {
    api(projects.sketchCore)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testSingletonView)
    androidTestImplementation(projects.internal.testView)
}