plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.test.utils.compose")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.internal.testUtilsComposeCore)
            api(projects.internal.testUtils)
        }
    }
}