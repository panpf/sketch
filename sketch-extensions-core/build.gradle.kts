plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

group = property("GROUP").toString()
version = property("versionName").toString()

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        androidInstrumentedTest {
            dependencies {
                implementation(project(":internal:test-utils"))
            }
        }
        commonMain {
            dependencies {
                api(project(":sketch-core"))
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

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.core")