plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-atomicfu")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.stdlib)
            api(libs.kotlinx.coroutines.core)
            api(libs.ktor.client.core)
            api(libs.okio)
            api(libs.skiko)
//                compileOnly(libs.composeStableMarker)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            api(libs.androidx.exifinterface)
            api(libs.androidx.annotation)
            api(libs.androidx.appcompat.resources)
            api(libs.androidx.core)
            api(libs.androidx.exifinterface)
            api(libs.androidx.lifecycle.runtime)
            api(libs.kotlinx.coroutines.android)
            api(libs.ktor.client.android)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testUtils)
        }
        desktopMain.dependencies {
            api(libs.kotlinx.coroutines.swing)
            api(libs.ktor.client.java)
        }
        iosMain.dependencies {
            api(libs.ktor.client.ios)
        }
        jsMain.dependencies {
            api(libs.ktor.client.js)
        }
        wasmJsMain.dependencies {
            api(libs.ktor.client.core.wasm)
            api(libs.ktor.client.wasmJs)
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