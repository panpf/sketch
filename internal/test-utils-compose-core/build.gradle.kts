@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.test.utils.compose.core")

compose.resources {
    packageOfResClass = "com.github.panpf.sketch.test.utils.compose.core.resources"
    publicResClass = true
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.internal.testUtilsCore)
            api(projects.sketchComposeCore)
            api(compose.foundation)
            api(compose.ui)
            api(compose.uiTest)
            api(compose.components.resources)
        }
        androidMain.dependencies {
            api(libs.androidx.compose.ui.test.junit4.android)
            api(libs.androidx.compose.ui.test.manifest)
        }
    }
}