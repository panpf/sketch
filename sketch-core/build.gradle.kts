plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-atomicfu")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.sketchHttpCore)
            api(libs.kotlin.stdlib)
            api(libs.kotlinx.coroutines.core)
            api(libs.okio)
            api(libs.skiko)
//                compileOnly(libs.composeStableMarker)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            api(libs.androidx.annotation)
            api(libs.androidx.appcompat.resources)
            api(libs.androidx.core)
            api(libs.androidx.exifinterface)
            api(libs.androidx.lifecycle.runtime)
            api(libs.kotlinx.coroutines.android)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testUtils)
        }
        desktopMain.dependencies {
            api(libs.kotlinx.coroutines.swing)
        }
        nonJvmCommonMain.dependencies {
            api(projects.sketchHttpKtor)
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