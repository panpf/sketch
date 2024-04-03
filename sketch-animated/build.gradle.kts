plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

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

        nonAndroidMain {
            dependencies {
            }
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.animated")