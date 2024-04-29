plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.view.ability")

dependencies {
    api(projects.sketchCore)
    api(libs.androidx.appcompat)
    androidTestImplementation(projects.internal.testUtils)
}