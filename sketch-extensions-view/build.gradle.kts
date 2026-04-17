plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.view")

dependencies {
    api(projects.sketchExtensionsCore)
    api(projects.sketchExtensionsViewability)
    api(projects.sketchViewCore)
    api(libs.androidx.recyclerview)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testHttp)
    androidTestImplementation(projects.internal.testSingletonView)
}