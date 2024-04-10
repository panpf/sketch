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
                api(libs.kotlinx.coroutines.android)
                api(libs.androidx.exifinterface)
                api(libs.androidx.annotation)
                api(libs.androidx.appcompat.resources)
                api(libs.androidx.core)
                api(libs.androidx.exifinterface)
                api(libs.androidx.lifecycle.runtime)
                api(libs.ktor.client.android)
            }
        }
        androidInstrumentedTest {
            dependencies {
//                implementation(project(":internal:test-utils"))
            }
        }

        commonMain {
            dependencies {
//                api(libs.kotlin.stdlib.jdk8)
//                api(libs.androidx.annotation)
                api(libs.kotlinx.coroutines.core)
//                compileOnly(libs.composeStableMarker)
                api(libs.okio)
                api(libs.ktor.client.core)
                api(libs.skiko)
//                api(libs.urlencoder)
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
                api(libs.kotlinx.coroutines.swing)
//                api(libs.metadataExtractor)
                api(libs.ktor.client.java)
            }
        }
        desktopTest {
            dependencies {
            }
        }

        iosMain {
            dependencies {
                api(libs.ktor.client.ios)
            }
        }

        jsMain {
            dependencies {
                api(libs.ktor.client.js)
            }
        }

        wasmJsMain {
            dependencies {
                api(libs.ktor.client.core.wasm)
                api(libs.ktor.client.wasmJs)
            }
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.core") {
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${project.versionName}\"")
        buildConfigField("int", "VERSION_CODE", project.versionCode.toString())
    }
}