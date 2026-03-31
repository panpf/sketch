plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addMultiplatformTargets(KmpTarget.entries.toTypedArray())

kotlin {
    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.test.koin")

    sourceSets {
        commonMain.dependencies {
            api(projects.internal.test)
            api(libs.koin.test)
        }
    }
}