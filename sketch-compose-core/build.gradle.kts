plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.compose.core")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
            api(compose.foundation)
            api(compose.runtime)
            api(compose.ui)
            api(libs.jetbrains.lifecycle.runtime.compose)
        }

        commonTest.dependencies {
            implementation(projects.internal.testCompose)
            implementation(projects.internal.testHttp)
            implementation(projects.internal.testSingleton)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testCompose)
            implementation(projects.internal.testHttp)
            implementation(projects.internal.testSingleton)
        }
    }
}