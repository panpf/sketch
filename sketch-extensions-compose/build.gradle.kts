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
        commonMain {
            dependencies {
                api(project(":sketch-compose-core"))
                api(project(":sketch-extensions-core"))
                api(compose.foundation)
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

androidLibrary(nameSpace = "com.github.panpf.sketch.extensions.compose")