plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
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
            }
        }
        named("androidInstrumentedTest") {
            dependencies {
                implementation(project(":sketch-test"))
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
                implementation(libs.junit)
                implementation(libs.panpf.tools4j.test)
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

        named("nonAndroidMain") {
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.github.panpf.sketch.gif"
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