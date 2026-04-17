plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(KmpTarget.entries.toTypedArray())
kmpAndroidLibrary(nameSpace = "com.github.panpf.sketch.singleton")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
        }
        androidDeviceTest.dependencies {
            implementation(projects.internal.test)
        }
    }
}