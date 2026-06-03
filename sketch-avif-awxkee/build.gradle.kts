plugins {
    id("com.android.library")
}

androidLibrary(nameSpace = "com.github.panpf.sketch.avif.awxkee") {
    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    api(projects.sketchCore)
    api(libs.avifcoder)

    androidTestImplementation(projects.internal.test)
    androidTestImplementation(projects.internal.testSingleton)
}