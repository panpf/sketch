plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.test.singleton")

dependencies {
    api(project(":sketch-core"))
    api(project(":internal:test-utils-core"))
}