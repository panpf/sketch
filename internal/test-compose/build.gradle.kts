@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.test.compose")

compose.resources {
    packageOfResClass = "com.github.panpf.sketch.test.compose.resources"
    publicResClass = true
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.internal.test)
            api(projects.sketchComposeCore)
            api(libs.jetbrains.compose.foundation)
            api(libs.jetbrains.compose.ui)
            api(libs.jetbrains.compose.ui.test)
            api(libs.jetbrains.compose.components.resources)
        }
        androidMain.dependencies {
            api(libs.androidx.compose.ui.test.junit4.android)
            api(libs.androidx.compose.ui.test.manifest)
        }
    }
}