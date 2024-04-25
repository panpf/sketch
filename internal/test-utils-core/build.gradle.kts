plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-atomicfu")
}

addAllMultiplatformTargets()
androidLibrary(nameSpace = "com.github.panpf.sketch.test.utils.core")

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(kotlin("test-junit"))
            api(projects.sketchCore)
            api(projects.internal.images)
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.coroutines.test)
        }
        jvmCommonMain.dependencies {
            api(libs.junit)
            api(libs.panpf.tools4j.reflect)
            api(libs.panpf.tools4j.security)
            api(libs.panpf.tools4j.test)
        }
        androidMain.dependencies {
            api(projects.sketchViewCore)
            api(libs.androidx.fragment)
            api(libs.androidx.test.runner)
            api(libs.androidx.test.rules)
            api(libs.androidx.test.ext.junit)
            api(libs.panpf.tools4a.device)
            api(libs.panpf.tools4a.dimen)
            api(libs.panpf.tools4a.display)
            api(libs.panpf.tools4a.network)
            api(libs.panpf.tools4a.run)
            api(libs.panpf.tools4a.test)
        }
        desktopMain.dependencies {
            api(skikoAwtRuntimeDependency(libs.versions.skiko.get()))
            api(libs.appdirs)
        }
    }
}