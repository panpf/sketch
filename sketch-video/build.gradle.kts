plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.video")

dependencies {
    api(projects.sketchVideoCore)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testSingleton)
}