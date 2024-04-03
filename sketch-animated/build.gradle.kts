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

android {
    namespace = "com.github.panpf.sketch.animated"
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