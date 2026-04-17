plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(KmpTarget.entries.toTypedArray())
kmpAndroidLibrary(nameSpace = "com.github.panpf.sketch.compose")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchComposeCore)
            api(projects.sketchSingleton)
        }

        commonTest.dependencies {
            implementation(projects.internal.testCompose)
            implementation(projects.internal.testSingleton)
        }
    }
}