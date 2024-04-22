plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets(listOf(MultiplatformTargets.Android, MultiplatformTargets.Desktop))

kotlin {
    sourceSets {
        jvmCommonMain.dependencies {
            api(projects.sketchHttpCore)
            api(libs.okhttp3)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.http.okhttp")