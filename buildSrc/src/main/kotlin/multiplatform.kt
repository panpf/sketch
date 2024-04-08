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
 */
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
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

//            if(targets == null || targets.contains(MultiplatformTargets.WasmJs)) {
//            @OptIn(ExperimentalWasmDsl::class)
//            wasmJs {
//                // TODO: Fix wasm tests.
//                browser {
//                    testTask {
//                        enabled = false
//                    }
//                }
//                nodejs {
//                    testTask {
//                        enabled = false
//                    }
//                }
//                binaries.executable()
//                binaries.library()
//            }
//            }

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

        if (targets == null || targets.contains(MultiplatformTargets.Js) || targets.contains(MultiplatformTargets.WasmJs)) {
            applyKotlinJsImplicitDependencyWorkaround()
        }
//        if(targets == null || targets.contains(MultiplatformTargets.WasmJs)) {
//        createSkikoWasmJsRuntimeDependency()
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

val NamedDomainObjectContainer<KotlinSourceSet>.jsCommonMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("jsCommonMain")

val NamedDomainObjectContainer<KotlinSourceSet>.nonJsCommonMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named("nonJsCommonMain")

// https://youtrack.jetbrains.com/issue/KT-56025
fun Project.applyKotlinJsImplicitDependencyWorkaround() {
    tasks.invoke {
        val configureJs: Task.() -> Unit = {
            dependsOn(named("jsDevelopmentLibraryCompileSync"))
            dependsOn(named("jsDevelopmentExecutableCompileSync"))
            dependsOn(named("jsProductionLibraryCompileSync"))
            dependsOn(named("jsProductionExecutableCompileSync"))
            dependsOn(named("jsTestTestDevelopmentExecutableCompileSync"))

            dependsOn(getByPath(":sketch:jsDevelopmentLibraryCompileSync"))
            dependsOn(getByPath(":sketch:jsDevelopmentExecutableCompileSync"))
            dependsOn(getByPath(":sketch:jsProductionLibraryCompileSync"))
            dependsOn(getByPath(":sketch:jsProductionExecutableCompileSync"))
            dependsOn(getByPath(":sketch:jsTestTestDevelopmentExecutableCompileSync"))
        }
        named("jsBrowserProductionWebpack").configure(configureJs)
        named("jsBrowserProductionLibraryPrepare").configure(configureJs)
        named("jsNodeProductionLibraryPrepare").configure(configureJs)

//        val configureWasmJs: Task.() -> Unit = {
//            dependsOn(named("wasmJsDevelopmentLibraryCompileSync"))
//            dependsOn(named("wasmJsDevelopmentExecutableCompileSync"))
//            dependsOn(named("wasmJsProductionLibraryCompileSync"))
//            dependsOn(named("wasmJsProductionExecutableCompileSync"))
//            dependsOn(named("wasmJsTestTestDevelopmentExecutableCompileSync"))
//
//            dependsOn(getByPath(":sketch:wasmJsDevelopmentLibraryCompileSync"))
//            dependsOn(getByPath(":sketch:wasmJsDevelopmentExecutableCompileSync"))
//            dependsOn(getByPath(":sketch:wasmJsProductionLibraryCompileSync"))
//            dependsOn(getByPath(":sketch:wasmJsProductionExecutableCompileSync"))
//            dependsOn(getByPath(":sketch:wasmJsTestTestDevelopmentExecutableCompileSync"))
//        }
//        named("wasmJsBrowserProductionWebpack").configure(configureWasmJs)
//        named("wasmJsBrowserProductionLibraryPrepare").configure(configureWasmJs)
//        named("wasmJsNodeProductionLibraryPrepare").configure(configureWasmJs)
//        named("wasmJsBrowserProductionExecutableDistributeResources").configure {
//            dependsOn(named("wasmJsDevelopmentLibraryCompileSync"))
//            dependsOn(named("wasmJsDevelopmentExecutableCompileSync"))
//            dependsOn(named("wasmJsProductionLibraryCompileSync"))
//            dependsOn(named("wasmJsProductionExecutableCompileSync"))
//            dependsOn(named("wasmJsTestTestDevelopmentExecutableCompileSync"))
//        }
    }
}

// https://youtrack.jetbrains.com/issue/KTOR-5587
fun Project.applyKtorWasmWorkaround(version: String) {
    repositories {
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
    configurations.all {
        if (name.startsWith("wasmJs")) {
            resolutionStrategy.eachDependency {
                if (requested.group.startsWith("io.ktor") &&
                    requested.name.startsWith("ktor-client-")
                ) {
                    useVersion(version)
                }
            }
        }
    }
}
