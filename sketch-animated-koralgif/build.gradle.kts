plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.animated.koralgif")

dependencies {
    api(project(":sketch-animated"))
    api(libs.androidgifdrawable)
    androidTestImplementation(project(":internal:test-utils"))
}