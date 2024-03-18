import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-atomicfu")
}

kotlin {
    applyMyHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")
        compilations.configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm("desktop") {
        compilations.configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sourceSets {
        named("androidMain") {
            dependencies {
                api(libs.kotlinx.coroutines.android)
                api(libs.androidx.exifinterface)
                api(libs.androidx.annotation)
                api(libs.androidx.appcompat.resources)
                api(libs.androidx.core)
                api(libs.androidx.exifinterface)
                api(libs.androidx.lifecycle.runtime)
                implementation(libs.ktor.client.android)
            }
        }
        named("androidInstrumentedTest") {
            dependencies {
                implementation(project(":sketch-test"))
            }
        }

        named("commonMain") {
            dependencies {
//                api(libs.kotlin.stdlib.jdk8)
                api(libs.androidx.annotation)
                api(libs.kotlinx.coroutines.core)
                compileOnly(libs.composeStableMarker)
                api(libs.okio)
                api(libs.ktor.client.core)
//                api(libs.skiko)
            }
        }
        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.panpf.tools4j.test)
            }
        }

        named("desktopMain") {
            dependencies {
                api(libs.kotlinx.coroutines.swing)
                api(libs.metadataExtractor)
                implementation(libs.ktor.client.java)
            }
        }

        named("desktopTest") {
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.github.panpf.sketch.core"
    compileSdk = property("compileSdk").toString().toInt()

    defaultConfig {
        minSdk = property("minSdk").toString().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "VERSION_NAME", "\"${property("versionName").toString()}\"")
        buildConfigField("int", "VERSION_CODE", property("versionCode").toString())
    }

    buildFeatures {
        buildConfig = true
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