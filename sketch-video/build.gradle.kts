plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.video")

dependencies {
    api(project(":sketch-core"))
    androidTestImplementation(project(":internal:test-utils"))
}