/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.androidLibrary(
    nameSpace: String,
    action: LibraryExtension.() -> Unit = {},
) = android<LibraryExtension> {
    namespace = nameSpace
    compileSdk = project.compileSdk
    defaultConfig {
        minSdk = project.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Compose Multiplatform 1.8.0 must use JVM target 11+, and Android View also requires 1.8+
    val (version, target) = if (plugins.findPlugin("org.jetbrains.kotlin.plugin.compose") != null) {
        JavaVersion.VERSION_11 to JvmTarget.JVM_11
    } else {
        JavaVersion.VERSION_1_8 to JvmTarget.JVM_1_8
    }
    compileOptions {
        sourceCompatibility = version
        targetCompatibility = version
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(target)
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all { test ->
            test.testLogging {
                exceptionFormat = TestExceptionFormat.FULL
                showExceptions = true
                showStackTraces = true
                showCauses = false
            }
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    packaging {
        resources.pickFirsts += listOf(
            "META-INF/AL2.0",
            "META-INF/LGPL2.1",
            "META-INF/*kotlin_module",
        )
    }

    action()
}

fun Project.androidApplication(
    nameSpace: String,
    applicationId: String = nameSpace,
    action: ApplicationExtension.() -> Unit = {},
) = android<ApplicationExtension> {
    namespace = nameSpace
    compileSdk = project.compileSdk
    defaultConfig {
        this.applicationId = applicationId
        versionCode = project.versionCode
        versionName = project.versionName
        vectorDrawables.useSupportLibrary = true
        minSdk = project.minSdk
        targetSdk = project.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Compose Multiplatform 1.8.0 must use JVM target 11+, and Android View also requires 1.8+
    val (version, target) = if (plugins.findPlugin("org.jetbrains.kotlin.plugin.compose") != null) {
        JavaVersion.VERSION_11 to JvmTarget.JVM_11
    } else {
        JavaVersion.VERSION_1_8 to JvmTarget.JVM_1_8
    }
    compileOptions {
        sourceCompatibility = version
        targetCompatibility = version
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(target)
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    packaging {
        resources.pickFirsts += listOf(
            "META-INF/AL2.0",
            "META-INF/LGPL2.1",
            "META-INF/*kotlin_module",
        )
    }

    action()
}

fun KotlinMultiplatformExtension.androidKmpLibrary(
    nameSpace: String,
    action: KotlinMultiplatformAndroidLibraryExtension.() -> Unit = {},
) = androidLibrary {
    namespace = nameSpace
    compileSdk = project.compileSdk
    minSdk = project.minSdk

    // Enable Android resource processing. Multiplatform library modules do not enable this by default.
    androidResources {
        enable = true
    }

    // Opt-in to enable and configure host-side (unit) tests. Multiplatform library modules do not enable this by default.
    withHostTest {
        isIncludeAndroidResources = true
        enableCoverage = true
    }
    // Opt-in to enable and configure device-side (instrumented) tests. Multiplatform library modules do not enable this by default.
    withDeviceTest {
        instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        execution = "HOST"
        enableCoverage = true
    }

    packaging {
        resources.pickFirsts += listOf(
            "META-INF/AL2.0",
            "META-INF/LGPL2.1",
            "META-INF/*kotlin_module",
        )
    }
    action()
}

private fun <T : CommonExtension> Project.android(
    action: T.() -> Unit
) {
    extensions.configure("android", action)
}

internal fun KotlinMultiplatformExtension.androidLibrary(
    action: KotlinMultiplatformAndroidLibraryExtension.() -> Unit
) {
    extensions.configure("androidLibrary", action)
}