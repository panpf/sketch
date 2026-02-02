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
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

enum class MultiplatformTargets {
    Android,
    Desktop,
    Js,
    WasmJs,
    IosX64,
    IosArm64,
    IosSimulatorArm64,
//    MacosX64,
//    MacosArm64
}

fun Project.addMultiplatformTargets(targets: Array<MultiplatformTargets>) {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            applyMyHierarchyTemplate()

            if (targets.contains(MultiplatformTargets.Android)) {
                androidLibrary {}
            }

            if (targets.contains(MultiplatformTargets.Desktop)) {
                jvm("desktop")
            }

            if (targets.contains(MultiplatformTargets.Js)) {
                js {
                    browser {
                        testTask {
                            enabled = false
                            useKarma {
                                useChrome()
                            }
                        }
                    }
                    nodejs {
                        testTask {
                            useMocha {
                                timeout = "60s"
                            }
                        }
                    }
                    binaries.executable()
                    binaries.library()
                }
            }

            if (targets.contains(MultiplatformTargets.WasmJs)) {
                @OptIn(ExperimentalWasmDsl::class)
                wasmJs {
                    // TODO: Fix wasm tests.
                    browser {
                        testTask {
                            enabled = false
                        }
                    }
                    nodejs {
                        testTask {
                            enabled = false
                        }
                    }
                    binaries.executable()
                    binaries.library()
                }
            }

            if (targets.contains(MultiplatformTargets.IosX64)) {
                iosX64()
            }
            if (targets.contains(MultiplatformTargets.IosArm64)) {
                iosArm64()
            }
            if (targets.contains(MultiplatformTargets.IosSimulatorArm64)) {
                iosSimulatorArm64()
            }

//            if (targets.contains(MultiplatformTargets.MacosX64)) {
//                macosX64()
//            }
//            if (targets.contains(MultiplatformTargets.MacosArm64)) {
//                macosArm64()
//            }
        }

        if (targets.contains(MultiplatformTargets.Js)) {
            applyKotlinJsImplicitDependencyWorkaround()
        }
        if (targets.contains(MultiplatformTargets.WasmJs)) {
            applyKotlinWasmJsImplicitDependencyWorkaround()

            // An error occurs when compiling js or wasmJs:
            // Resolving dependency configuration 'androidDebugAndroidTestCompilationApi' is not allowed as it is defined as 'canBeResolved=false'.
            // Instead, a resolvable ('canBeResolved=true') dependency configuration that extends 'androidDebugAndroidTestCompilationApi' should be resolved.
//            createSkikoWasmJsRuntimeDependency()
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
        named("jsNodeProductionLibraryDistribution").configure(configureJs)
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
        named("wasmJsNodeProductionLibraryDistribution").configure(configureWasmJs)
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