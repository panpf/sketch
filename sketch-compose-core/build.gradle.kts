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
        named("androidMain") {
            dependencies {
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
                api(compose.foundation)
                api(compose.ui)
                api(compose.components.resources)
            }
        }
        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
//                implementation(libs.junit)
//                implementation(libs.panpf.tools4j.test)
            }
        }
    }
}

compose {
    kotlinCompilerPlugin = libs.jetbrains.compose.compiler.get().toString()
}

android {
    namespace = "com.github.panpf.sketch.compose.core"
    compileSdk = property("compileSdk").toString().toInt()

    defaultConfig {
        minSdk = property("minSdk21").toString().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
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

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.jetbrains.compose.compiler.get()
    }
}