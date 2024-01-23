plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.github.panpf.sketch.test.utils"
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

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(":sketch-core"))
    api(project(":sketch-resources"))
    api(libs.androidx.fragment)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.androidx.test.ext.junit)
    api(libs.junit)
    api(libs.panpf.tools4a.device)
    api(libs.panpf.tools4a.dimen)
    api(libs.panpf.tools4a.display)
    api(libs.panpf.tools4a.network)
    api(libs.panpf.tools4a.run)
    api(libs.panpf.tools4a.test)
    api(libs.panpf.tools4j.reflect)
    api(libs.panpf.tools4j.security)
    api(libs.panpf.tools4j.test)
}