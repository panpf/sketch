plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
        }
        desktopMain {
            resources.srcDirs("files")
        }
        iosMain {
            resources.srcDirs("files")
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.images") {
    // Android does not support resources folders, so you can only use assets folders
    sourceSets["main"].assets.srcDirs("files")
}