plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets(
    MultiplatformTargets.Android,
    MultiplatformTargets.IosX64,
    MultiplatformTargets.IosArm64,
    MultiplatformTargets.IosSimulatorArm64,
)

androidLibrary(nameSpace = "com.github.panpf.sketch.video.core")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}
