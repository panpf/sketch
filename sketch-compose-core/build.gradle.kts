plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

group = property("GROUP").toString()
version = property("versionName").toString()

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        androidMain {
            dependencies {
            }
        }
        androidInstrumentedTest {
            dependencies {
                implementation(project(":internal:test-utils"))
            }
        }

        commonMain {
            dependencies {
                api(project(":sketch-core"))
                api(compose.foundation)
                api(compose.ui)
                api(compose.components.resources)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
//                implementation(libs.junit)
//                implementation(libs.panpf.tools4j.test)
            }
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.compose.core")