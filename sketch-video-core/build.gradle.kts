plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(
    kmpTargets = arrayOf(
        KmpTarget.Android,
        KmpTarget.IosSimulatorArm64,
        KmpTarget.IosArm64,
    )
)
kmpAndroidLibrary(nameSpace = "com.github.panpf.sketch.video.core")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
        androidDeviceTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}