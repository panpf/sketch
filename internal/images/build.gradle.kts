plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
}

addMultiplatformTargets(MultiplatformTargets.entries.toTypedArray())

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.images")

    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
            api(projects.sketchHttpCore)
            api(projects.sketchComposeResources)
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.components.resources)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
        }
        androidDeviceTest.dependencies {
            implementation(projects.internal.test)
        }
    }
}

compose.resources {
    packageOfResClass = "com.github.panpf.sketch.images"
    publicResClass = true
}