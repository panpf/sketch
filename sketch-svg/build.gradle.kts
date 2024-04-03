plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

addAllMultiplatformTargets()

kotlin {
    sourceSets {
        named("androidMain") {
            dependencies {
                api(libs.androidsvg)
            }
        }
        named("androidInstrumentedTest") {
            dependencies {
                implementation(project(":internal:test-utils"))
            }
        }

        named("commonMain") {
            dependencies {
                api(project(":sketch-core"))
            }
        }
        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
//                implementation(libs.junit)
//                implementation(libs.panpf.tools4j.test)
            }
        }

        named("desktopMain") {
            dependencies {
            }
        }

        named("desktopTest") {
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.github.panpf.sketch.svg"
    compileSdk = property("compileSdk").toString().toInt()

    defaultConfig {
        minSdk = property("minSdk").toString().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        targetSdk = property("targetSdk").toString().toInt()
    }

    // Set both the Java and Kotlin compilers to target Java 8.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}