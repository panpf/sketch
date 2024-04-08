plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.view")

dependencies {
    api(project(":sketch"))
    api(project(":sketch-extensions-view-core"))
    androidTestImplementation(project(":internal:test-utils"))
}