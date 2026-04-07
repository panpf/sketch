plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
}

addMultiplatformTargets(KmpTarget.entries.toTypedArray())
kmpAndroidLibrary(nameSpace = "com.github.panpf.sketch.test.singleton")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.internal.test)
        }
        androidMain.dependencies {
            api(projects.internal.test)
        }
    }
}