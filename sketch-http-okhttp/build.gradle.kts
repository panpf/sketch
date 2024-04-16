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
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testUtils)
        }
        desktopTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.junit)
            implementation(libs.panpf.tools4j.test)
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.http.okhttp")