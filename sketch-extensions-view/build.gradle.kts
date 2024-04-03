plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.view") {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(":sketch"))
    api(project(":sketch-extensions-view-core"))
    androidTestImplementation(project(":internal:test-utils"))
}