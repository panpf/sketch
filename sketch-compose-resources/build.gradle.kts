plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

addAllMultiplatformTargets()

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

androidLibrary(nameSpace = "com.github.panpf.sketch.compose.resources")