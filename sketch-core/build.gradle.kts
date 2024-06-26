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
            api(libs.jetbrains.lifecycle.common)
        }
        androidMain.dependencies {
            api(libs.androidx.annotation)
            api(libs.androidx.appcompat.resources)
            api(libs.androidx.core)
            api(libs.androidx.exifinterface)
            api(libs.kotlinx.coroutines.android)
        }
        desktopMain.dependencies {
            api(libs.kotlinx.coroutines.swing)
            api(libs.appdirs)
        }
        nonAndroidMain.dependencies {
            api(libs.skiko)
        }
        nonJvmCommonMain.dependencies {
            api(projects.sketchHttpKtor)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testUtils)
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