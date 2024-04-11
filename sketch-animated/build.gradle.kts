plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.testUtils)
        }
        commonMain.dependencies {
            api(projects.sketchCore)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
//                implementation(libs.junit)
//                implementation(libs.panpf.tools4j.test)
        }
    }
}

androidLibrary(nameSpace = "com.github.panpf.sketch.animated")