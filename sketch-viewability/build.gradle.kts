plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.viewability")

dependencies {
    api(project(":sketch-core"))
    api(libs.androidx.appcompat)
    androidTestImplementation(project(":internal:test-utils"))
}