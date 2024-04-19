plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.view.core")

dependencies {
    api(projects.sketchExtensionsCore)
    api(projects.sketchExtensionsViewAbility)
    api(libs.androidx.recyclerview)
    androidTestImplementation(projects.internal.testUtils)
}