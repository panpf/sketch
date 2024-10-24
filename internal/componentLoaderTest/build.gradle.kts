plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.componentloadertest")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.internal.test)
            api(projects.sketchAnimatedGif)
            api(projects.sketchAnimatedWebp)
            api(projects.sketchComposeResources)
            api(projects.sketchHttpKtor3)
            api(projects.sketchSvg)
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
            api(libs.ktor3.client.android)
        }
        desktopMain.dependencies {
            api(libs.ktor3.client.java)
        }
        iosMain.dependencies {
            api(libs.ktor3.client.darwin)
        }
        jsMain.dependencies {
            api(libs.ktor3.client.js)
        }
        wasmJsMain.dependencies {
            api(libs.ktor3.client.wasmJs)
        }
    }
}