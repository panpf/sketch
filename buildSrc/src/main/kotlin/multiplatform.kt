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

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

enum class KmpTarget {
    Android,
    Desktop,
    Js,
    WasmJs,
    Ios,
}

fun Project.addMultiplatformTargets(kmpTargets: Array<KmpTarget>) {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            applyMyHierarchyTemplate()

            if (kmpTargets.contains(KmpTarget.Android)) {
                androidLibrary {}
            }

            if (kmpTargets.contains(KmpTarget.Desktop)) {
                jvm("desktop")
            }

            if (kmpTargets.contains(KmpTarget.Js)) {
                js {
                    browser {
                        testTask {
                            useKarma {
                                useChromeHeadless()
                            }
                        }
                    }
                    binaries.executable()
                    binaries.library()
                }
            }

            if (kmpTargets.contains(KmpTarget.WasmJs)) {
                @OptIn(ExperimentalWasmDsl::class)
                wasmJs {
                    browser {
                        testTask {
                            useKarma {
                                useChromeHeadless()
                            }
                        }
                    }
                    binaries.executable()
                    binaries.library()
                }
            }

            if (kmpTargets.contains(KmpTarget.Ios)) {
                iosArm64()
                iosSimulatorArm64()
            }
        }

        if (kmpTargets.contains(KmpTarget.Ios)) {
            copyResourcesToIosTestBin()
        }

        if (kmpTargets.contains(KmpTarget.Js)) {
            applyKotlinJsImplicitDependencyWorkaround()
        }
        if (kmpTargets.contains(KmpTarget.WasmJs)) {
            applyKotlinWasmJsImplicitDependencyWorkaround()
        }

        // Cannot find module './skiko.mjs'
        if (kmpTargets.contains(KmpTarget.Js) || kmpTargets.contains(KmpTarget.WasmJs)) {
            createSkikoWasmJsRuntimeDependency()
        }
    }
}

/**
 * Although the iosTest environment is configured with a dependency on the images module,
 * it still cannot access the resource files in the images module.
 * The temporary solution is to copy the resource files to the bin directory of iosTest before executing the test task.
 */
fun Project.copyResourcesToIosTestBin() {
    val copyComposeResourcesTask = tasks.register(
        /* name = */ "copyComposeResourcesToIosTestBin",
        /* type = */ Copy::class.java
    ) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Copy compose resources to iOS simulator build bin directory for tests"
        duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.INCLUDE

        val fromDir = file("${project.rootDir}/internal/images/src/commonMain/composeResources")
        if (!fromDir.exists()) {
            throw IllegalStateException("Source directory '$fromDir' does not exist. Please check the path.")
        }
        from(fromDir)

        val destDir =
            layout.buildDirectory.dir("bin/iosSimulatorArm64/debugTest/compose-resources/composeResources/com.github.panpf.sketch.images")
        into(destDir)
        println("Copyed compose resources from '$fromDir' to '${destDir.get()}'")
    }

    val copyKotlinResourcesTask = tasks.register(
        /* name = */ "copyKotlinResourcesToIosTestBin",
        /* type = */ Copy::class.java
    ) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Copy kotlin resources to iOS simulator build bin directory for tests"
        duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.INCLUDE

        val fromDir = file("${project.rootDir}/internal/images/src/iosMain/resources")
        if (!fromDir.exists()) {
            throw IllegalStateException("Source directory '$fromDir' does not exist. Please check the path.")
        }
        from(fromDir)

        val destDir =
            layout.buildDirectory.dir("bin/iosSimulatorArm64/debugTest/compose-resources")
        into(destDir)
        println("Copyed kotlin resources from '$fromDir' to '${destDir.get()}'")
    }

    afterEvaluate {
        val testTaskName = "iosSimulatorArm64Test"
        tasks.findByName(testTaskName)?.let { testTask ->
            testTask.dependsOn(copyComposeResourcesTask)
            testTask.dependsOn(copyKotlinResourcesTask)
        }
    }
}

// https://youtrack.jetbrains.com/issue/KT-56025
fun Project.applyKotlinJsImplicitDependencyWorkaround() {
    tasks {
        val configureJs: Task.() -> Unit = {
            dependsOn(named("jsDevelopmentLibraryCompileSync"))
            dependsOn(named("jsDevelopmentExecutableCompileSync"))
            dependsOn(named("jsProductionLibraryCompileSync"))
            dependsOn(named("jsProductionExecutableCompileSync"))
            dependsOn(named("jsTestTestDevelopmentExecutableCompileSync"))

            dependsOn(getByPath(":sketch-core:jsDevelopmentLibraryCompileSync"))
            dependsOn(getByPath(":sketch-core:jsDevelopmentExecutableCompileSync"))
            dependsOn(getByPath(":sketch-core:jsProductionLibraryCompileSync"))
            dependsOn(getByPath(":sketch-core:jsProductionExecutableCompileSync"))
            dependsOn(getByPath(":sketch-core:jsTestTestDevelopmentExecutableCompileSync"))
        }
        named("jsBrowserProductionWebpack").configure(configureJs)
        named("jsBrowserProductionLibraryDistribution").configure(configureJs)
    }
}

// https://youtrack.jetbrains.com/issue/KT-56025
fun Project.applyKotlinWasmJsImplicitDependencyWorkaround() {
    tasks {
        val configureWasmJs: Task.() -> Unit = {
            dependsOn(named("wasmJsDevelopmentLibraryCompileSync"))
            dependsOn(named("wasmJsDevelopmentExecutableCompileSync"))
            dependsOn(named("wasmJsProductionLibraryCompileSync"))
            dependsOn(named("wasmJsProductionExecutableCompileSync"))
            dependsOn(named("wasmJsTestTestDevelopmentExecutableCompileSync"))

            dependsOn(getByPath(":sketch-core:wasmJsDevelopmentLibraryCompileSync"))
            dependsOn(getByPath(":sketch-core:wasmJsDevelopmentExecutableCompileSync"))
            dependsOn(getByPath(":sketch-core:wasmJsProductionLibraryCompileSync"))
            dependsOn(getByPath(":sketch-core:wasmJsProductionExecutableCompileSync"))
            dependsOn(getByPath(":sketch-core:wasmJsTestTestDevelopmentExecutableCompileSync"))
        }
        named("wasmJsBrowserProductionWebpack").configure(configureWasmJs)
        named("wasmJsBrowserProductionLibraryDistribution").configure(configureWasmJs)
    }
}

val NamedDomainObjectContainer<KotlinSourceSet>.androidUnitTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidUnitTest")

val NamedDomainObjectContainer<KotlinSourceSet>.androidInstrumentedTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidInstrumentedTest")

val NamedDomainObjectContainer<KotlinSourceSet>.androidDeviceTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidDeviceTest")

val NamedDomainObjectContainer<KotlinSourceSet>.desktopMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("desktopMain")

val NamedDomainObjectContainer<KotlinSourceSet>.desktopTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("desktopTest")

val NamedDomainObjectContainer<KotlinSourceSet>.nonAndroidMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("nonAndroidMain")

val NamedDomainObjectContainer<KotlinSourceSet>.nonAndroidTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("nonAndroidTest")

val NamedDomainObjectContainer<KotlinSourceSet>.jvmCommonMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("jvmCommonMain")

val NamedDomainObjectContainer<KotlinSourceSet>.jvmCommonTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("jvmCommonTest")

val NamedDomainObjectContainer<KotlinSourceSet>.nonJvmCommonMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("nonJvmCommonMain")

val NamedDomainObjectContainer<KotlinSourceSet>.nonJvmCommonTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("nonJvmCommonTest")

val NamedDomainObjectContainer<KotlinSourceSet>.jsCommonMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("jsCommonMain")

val NamedDomainObjectContainer<KotlinSourceSet>.nonJsCommonMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("nonJsCommonMain")

val NamedDomainObjectContainer<KotlinSourceSet>.wasmJsMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("wasmJsMain")

val NamedDomainObjectContainer<KotlinSourceSet>.nonWasmJsMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("nonWasmJsMain")