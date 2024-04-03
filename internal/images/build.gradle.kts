plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":sketch-core"))
            }
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.images") {
    // The files in the commonMain.resources folder will not be packaged into aar, so you need to configure it in the androidMain.resources folder.
    sourceSets["main"].assets.srcDirs("src/commonMain/resources")
}