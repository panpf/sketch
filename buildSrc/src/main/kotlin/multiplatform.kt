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
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun Project.addAllMultiplatformTargets() {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            applyMyHierarchyTemplate()

            val isAndroidApp = plugins.hasPlugin("com.android.application")
            val isAndroidLibrary = plugins.hasPlugin("com.android.library")
            if (isAndroidApp || isAndroidLibrary) {
                androidTarget {
                    if (isAndroidLibrary) {
                        publishLibraryVariants("release")
                    }
                    compilations.configureEach {
                        kotlinOptions {
                            jvmTarget = "1.8"
                        }
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

//            js {
//                browser()
//                nodejs {
//                    testTask {
//                        useMocha {
//                            timeout = "60s"
//                        }
//                    }
//                }
//                binaries.executable()
//                binaries.library()
//            }
//
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

            iosX64()
            iosArm64()
            iosSimulatorArm64()

            macosX64()
            macosArm64()
        }

//        applyKotlinJsImplicitDependencyWorkaround()
//        createSkikoWasmJsRuntimeDependency()
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

//// https://youtrack.jetbrains.com/issue/KT-56025
//fun Project.applyKotlinJsImplicitDependencyWorkaround() {
//    tasks {
//        val configureJs: Task.() -> Unit = {
//            dependsOn(named("jsDevelopmentLibraryCompileSync"))
//            dependsOn(named("jsDevelopmentExecutableCompileSync"))
//            dependsOn(named("jsProductionLibraryCompileSync"))
//            dependsOn(named("jsProductionExecutableCompileSync"))
//            dependsOn(named("jsTestTestDevelopmentExecutableCompileSync"))
//
//            dependsOn(getByPath(":coil:jsDevelopmentLibraryCompileSync"))
//            dependsOn(getByPath(":coil:jsDevelopmentExecutableCompileSync"))
//            dependsOn(getByPath(":coil:jsProductionLibraryCompileSync"))
//            dependsOn(getByPath(":coil:jsProductionExecutableCompileSync"))
//            dependsOn(getByPath(":coil:jsTestTestDevelopmentExecutableCompileSync"))
//        }
//        named("jsBrowserProductionWebpack").configure(configureJs)
//        named("jsBrowserProductionLibraryPrepare").configure(configureJs)
//        named("jsNodeProductionLibraryPrepare").configure(configureJs)
//
//        val configureWasmJs: Task.() -> Unit = {
//            dependsOn(named("wasmJsDevelopmentLibraryCompileSync"))
//            dependsOn(named("wasmJsDevelopmentExecutableCompileSync"))
//            dependsOn(named("wasmJsProductionLibraryCompileSync"))
//            dependsOn(named("wasmJsProductionExecutableCompileSync"))
//            dependsOn(named("wasmJsTestTestDevelopmentExecutableCompileSync"))
//
//            dependsOn(getByPath(":coil:wasmJsDevelopmentLibraryCompileSync"))
//            dependsOn(getByPath(":coil:wasmJsDevelopmentExecutableCompileSync"))
//            dependsOn(getByPath(":coil:wasmJsProductionLibraryCompileSync"))
//            dependsOn(getByPath(":coil:wasmJsProductionExecutableCompileSync"))
//            dependsOn(getByPath(":coil:wasmJsTestTestDevelopmentExecutableCompileSync"))
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
//    }
//}
//
//// https://youtrack.jetbrains.com/issue/KTOR-5587
//fun Project.applyKtorWasmWorkaround(version: String) {
//    repositories {
//        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
//    }
//    configurations.all {
//        if (name.startsWith("wasmJs")) {
//            resolutionStrategy.eachDependency {
//                if (requested.group.startsWith("io.ktor") &&
//                    requested.name.startsWith("ktor-client-")) {
//                    useVersion(version)
//                }
//            }
//        }
//    }
//}
