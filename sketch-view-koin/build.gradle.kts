plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.view.koin")

dependencies {
    api(projects.sketchViewCore)
    api(projects.sketchKoin)

    androidTestImplementation(projects.internal.testKoin)
    androidTestImplementation(projects.internal.testView)
}