plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.animated.heif")

dependencies {
    api(projects.sketchAnimatedCore)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testSingleton)
}