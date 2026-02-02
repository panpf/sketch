plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.animated.gif.koral")

dependencies {
    api(projects.sketchAnimatedCore)
    api(libs.androidgifdrawable)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testSingleton)
}