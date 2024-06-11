/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ------------------------------------------------------------------------
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

enum class MultiplatformTargets {
    Android,
    Desktop,
    Js,
    WasmJs,
    IosX64,
    IosArm64,
    IosSimulatorArm64,
    MacosX64,
    MacosArm64
}

fun Project.addAllMultiplatformTargets(targets: List<MultiplatformTargets>? = null) {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            applyMyHierarchyTemplate()

            if (targets == null || targets.contains(MultiplatformTargets.Android)) {
                val isAndroidApp = plugins.hasPlugin("com.android.application")
                val isAndroidLibrary = plugins.hasPlugin("com.android.library")
                if (isAndroidApp || isAndroidLibrary) {
                    androidTarget {
                        if (isAndroidLibrary) {
                            publishLibraryVariants("release")
                        }
                    }
                }
            }

            if (targets == null || targets.contains(MultiplatformTargets.Desktop)) {
                jvm("desktop")
            }

            if (targets == null || targets.contains(MultiplatformTargets.Js)) {
                js {
                    browser()
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

            if (targets == null || targets.contains(MultiplatformTargets.WasmJs)) {
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

            if (targets == null || targets.contains(MultiplatformTargets.IosX64)) {
                iosX64()
            }
            if (targets == null || targets.contains(MultiplatformTargets.IosArm64)) {
                iosArm64()
            }
            if (targets == null || targets.contains(MultiplatformTargets.IosSimulatorArm64)) {
                iosSimulatorArm64()
            }

            if (targets == null || targets.contains(MultiplatformTargets.MacosX64)) {
                macosX64()
            }
            if (targets == null || targets.contains(MultiplatformTargets.MacosArm64)) {
                macosArm64()
            }
        }

        if (targets == null || targets.contains(MultiplatformTargets.Js)) {
            applyKotlinJsImplicitDependencyWorkaround()
        }
        if (targets == null || targets.contains(MultiplatformTargets.WasmJs)) {
            applyKotlinWasmJsImplicitDependencyWorkaround()
        }
        // An error occurs when compiling js or wasmJs:
        // Resolving dependency configuration 'androidDebugAndroidTestCompilationApi' is not allowed as it is defined as 'canBeResolved=false'.
        //Instead, a resolvable ('canBeResolved=true') dependency configuration that extends 'androidDebugAndroidTestCompilationApi' should be resolved.
//        if (targets == null || targets.contains(MultiplatformTargets.Js) || targets.contains(MultiplatformTargets.WasmJs)) {
//            createSkikoWasmJsRuntimeDependency()
//        }
    }
}

val NamedDomainObjectContainer<KotlinSourceSet>.androidUnitTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidUnitTest")

val NamedDomainObjectContainer<KotlinSourceSet>.androidInstrumentedTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("androidInstrumentedTest")

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

// https://youtrack.jetbrains.com/issue/KT-56025
fun Project.applyKotlinJsImplicitDependencyWorkaround() {
    tasks.invoke {
        val configureJs: Task.() -> Unit = {
            dependsOn(named("jsDevelopmentLibraryCompileSync"))
            dependsOn(named("jsDevelopmentExecutableCompileSync"))
            dependsOn(named("jsProductionLibraryCompileSync"))
            dependsOn(named("jsProductionExecutableCompileSync"))
            dependsOn(named("jsTestTestDevelopmentExecutableCompileSync"))

            dependsOn(getByPath(":sketch-singleton:jsDevelopmentLibraryCompileSync"))
            dependsOn(getByPath(":sketch-singleton:jsDevelopmentExecutableCompileSync"))
            dependsOn(getByPath(":sketch-singleton:jsProductionLibraryCompileSync"))
            dependsOn(getByPath(":sketch-singleton:jsProductionExecutableCompileSync"))
            dependsOn(getByPath(":sketch-singleton:jsTestTestDevelopmentExecutableCompileSync"))
        }
        named("jsBrowserProductionWebpack").configure(configureJs)
        named("jsBrowserProductionLibraryDistribution").configure(configureJs)
        named("jsNodeProductionLibraryDistribution").configure(configureJs)
    }
}

// https://youtrack.jetbrains.com/issue/KT-56025
fun Project.applyKotlinWasmJsImplicitDependencyWorkaround() {
    tasks.invoke {
        val configureWasmJs: Task.() -> Unit = {
            dependsOn(named("wasmJsDevelopmentLibraryCompileSync"))
            dependsOn(named("wasmJsDevelopmentExecutableCompileSync"))
            dependsOn(named("wasmJsProductionLibraryCompileSync"))
            dependsOn(named("wasmJsProductionExecutableCompileSync"))
            dependsOn(named("wasmJsTestTestDevelopmentExecutableCompileSync"))

            dependsOn(getByPath(":sketch-singleton:wasmJsDevelopmentLibraryCompileSync"))
            dependsOn(getByPath(":sketch-singleton:wasmJsDevelopmentExecutableCompileSync"))
            dependsOn(getByPath(":sketch-singleton:wasmJsProductionLibraryCompileSync"))
            dependsOn(getByPath(":sketch-singleton:wasmJsProductionExecutableCompileSync"))
            dependsOn(getByPath(":sketch-singleton:wasmJsTestTestDevelopmentExecutableCompileSync"))
        }
        named("wasmJsBrowserProductionWebpack").configure(configureWasmJs)
        named("wasmJsBrowserProductionLibraryDistribution").configure(configureWasmJs)
        named("wasmJsNodeProductionLibraryDistribution").configure(configureWasmJs)
    }
}