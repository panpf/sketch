plugins {
    id("com.android.library")
    id("com.codingfeline.buildkonfig")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
    id("org.jetbrains.kotlinx.kover")
}

addAllMultiplatformTargets()

androidLibrary(nameSpace = "com.github.panpf.sketch.core") {
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${project.versionName}\"")
        buildConfigField("int", "VERSION_CODE", project.versionCode.toString())
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.stdlib)
            api(libs.kotlinx.coroutines.core)
            api(libs.okio)
            api(libs.jetbrains.lifecycle.common)
        }
        androidMain.dependencies {
            api(libs.androidx.appcompat.resources)
            api(libs.androidx.core)
            api(libs.androidx.exifinterface)
            api(libs.kotlinx.coroutines.android)
        }
        desktopMain.dependencies {
            api(libs.appdirs)
            api(libs.kotlinx.coroutines.swing)
        }
        nonAndroidMain.dependencies {
            api(libs.skiko)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testHttp)
            implementation(projects.internal.testSingleton)
        }
        androidInstrumentedTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testHttp)
            implementation(projects.internal.testSingleton)
        }
    }
}

buildkonfig {
    packageName = "com.github.panpf.sketch.core"
    defaultConfigs {
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "VERSION_NAME",
            value = project.versionName,
            const = true
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "VERSION_CODE",
            value = project.versionCode.toString(),
            const = true
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SKIKO_VERSION_NAME",
            value = libs.versions.skiko.get(),
            const = true
        )
    }
}