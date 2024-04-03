plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        jvmCommonMain {
            dependencies {
                api(project(":sketch-core"))
                api(libs.okhttp3)
            }
        }

        androidInstrumentedTest {
            dependencies {
                implementation(project(":internal:test-utils"))
            }
        }
        desktopTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.panpf.tools4j.test)
            }
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.okhttp")