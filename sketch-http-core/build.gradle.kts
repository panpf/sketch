plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.stdlib)
            api(libs.kotlinx.coroutines.core)
            api(libs.okio)
        }

        commonTest.dependencies {
            implementation(kotlin("test-junit"))
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.http.core")