plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.compose.resources")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchComposeCore)
            api(compose.components.resources)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtilsCompose)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testUtilsCompose)
        }
    }
}