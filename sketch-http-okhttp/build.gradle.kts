plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.kover")
}

addMultiplatformTargets(arrayOf(KmpTarget.Android, KmpTarget.Desktop))
kmpAndroidLibrary(nameSpace = "com.github.panpf.sketch.http.okhttp")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchCore)
            api(projects.sketchHttpCore)
            api(libs.okhttp3)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}