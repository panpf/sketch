plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.viewability")

dependencies {
    api(projects.sketchCore)
    api(libs.androidx.appcompat)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testHttp)
    androidTestImplementation(projects.internal.testSingletonView)
}