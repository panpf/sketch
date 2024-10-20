plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.animated.heif")

dependencies {
    api(projects.sketchAnimatedCore)

    implementation(projects.internal.test)
    implementation(projects.internal.testSingleton)
}