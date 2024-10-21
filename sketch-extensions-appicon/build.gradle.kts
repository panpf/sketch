plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.appicon")

dependencies {
    api(projects.sketchCore)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testSingleton)
}