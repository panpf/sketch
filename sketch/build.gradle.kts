plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-atomicfu")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(libs.google.accompanist.drawablepainter)
            }
        }
        androidInstrumentedTest {
            dependencies {
                implementation(project(":internal:test-utils-core"))
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

androidLibrary(nameSpace = "com.github.panpf.sketch")