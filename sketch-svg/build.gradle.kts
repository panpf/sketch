plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                api(libs.androidsvg)
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
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
//                implementation(libs.junit)
//                implementation(libs.panpf.tools4j.test)
            }
        }

        desktopMain {
            dependencies {
            }
        }
        desktopTest {
            dependencies {
            }
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.svg")