plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.view.core")

dependencies {
    api(project(":sketch-extensions-core"))
    api(project(":sketch-viewability"))
    api(libs.androidx.recyclerview)
    androidTestImplementation(project(":internal:test-utils"))
}