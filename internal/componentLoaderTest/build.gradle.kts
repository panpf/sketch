plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
}

addMultiplatformTargets(MultiplatformTargets.entries.toTypedArray())

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.componentloadertest")

    sourceSets {
        commonMain.dependencies {
            api(projects.internal.test)
            api(projects.sketchAnimatedGif)
            api(projects.sketchAnimatedWebp)
            api(projects.sketchComposeResources)
            api(projects.sketchHttpKtor3)
            api(projects.sketchSvg)
            api(projects.sketchBlurhash)
        }
        jvmCommonMain.dependencies {
            api(projects.sketchHttpHurl)
            api(projects.sketchHttpOkhttp)
        }
        androidMain.dependencies {
            api(projects.sketchAnimatedGifKoral)
            api(projects.sketchAnimatedHeif)
            api(projects.sketchExtensionsApkicon)
            api(projects.sketchExtensionsAppicon)
            api(projects.sketchVideo)
            api(projects.sketchVideoFfmpeg)
        }
    }
}