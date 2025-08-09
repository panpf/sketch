plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.blurhash")

kotlin {
    sourceSets {
        commonMain.dependencies {
//            api(projects.sketchCore)
            api(projects.sketchComposeCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.testCompose)
            implementation(projects.internal.testSingleton)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testCompose)
            implementation(projects.internal.testSingleton)
        }
    }
}