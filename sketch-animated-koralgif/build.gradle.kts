plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.animated.koralgif")

dependencies {
    api(projects.sketchAnimated)
    api(libs.androidgifdrawable)

    androidTestImplementation(projects.internal.testUtils)
}