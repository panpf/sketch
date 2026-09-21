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

@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalKotlinGradlePluginApi::class)

import com.android.build.api.withAndroid
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

private val hierarchyTemplate = KotlinHierarchyTemplate {
    withSourceSetTree(
        KotlinSourceSetTree.main,
        KotlinSourceSetTree.test,
    )

    common {
        withCompilations { true }

        groupNonAndroid()
        groupIos()
        groupNonIos()

        groupNonJvm()
        groupMacos()
        groupNonMacos()

        groupNonJs()
        groupNonWasmJs()

        groupPhone()
        groupNonPhone()
        groupDesktop()
        groupNonDesktop()
        groupJvmCommon()
        groupNonJvmCommon()
        groupApple()
        groupNonApple()
        groupJsCommon()
        groupNonJsCommon()
    }
}

private fun KotlinHierarchyBuilder.groupNonAndroid() {
    group("nonAndroid") {
//        withAndroid()
        groupIos()
        withJvm()
        groupMacos()
        withJs()
        withWasmJs()
//        groupPhone()
        groupDesktop()
//        groupJvmCommon()
        groupApple()
        groupJsCommon()
    }
}

private fun KotlinHierarchyBuilder.groupIos() {
    group("ios") {
        withIos()
    }
}

private fun KotlinHierarchyBuilder.groupNonIos() {
    group("nonIos") {
        withAndroid()
//        groupIos()
        withJvm()
        groupMacos()
        withJs()
        withWasmJs()
//        groupPhone()
        groupDesktop()
        groupJvmCommon()
//        groupApple()
        groupJsCommon()
    }
}


private fun KotlinHierarchyBuilder.groupNonJvm() {
    group("nonJvm") {
        withAndroid()
        groupIos()
//        withJvm()
        groupMacos()
        withJs()
        withWasmJs()
        groupPhone()
//        groupDesktop()
//        groupJvmCommon()
        groupApple()
        groupJsCommon()
    }
}

private fun KotlinHierarchyBuilder.groupMacos() {
    group("macos") {
        withMacos()
    }
}

private fun KotlinHierarchyBuilder.groupNonMacos() {
    group("nonMacos") {
        withAndroid()
        groupIos()
        withJvm()
//        groupMacos()
        withJs()
        withWasmJs()
        groupPhone()
//        groupDesktop()
        groupJvmCommon()
//        groupApple()
        groupJsCommon()
    }
}


private fun KotlinHierarchyBuilder.groupNonJs() {
    group("nonJs") {
        withAndroid()
        groupIos()
        withJvm()
        groupMacos()
//        withJs()
        withWasmJs()
        groupPhone()
        groupDesktop()
        groupJvmCommon()
        groupApple()
//        groupJsCommon()
    }
}

private fun KotlinHierarchyBuilder.groupNonWasmJs() {
    group("nonWasmJs") {
        withAndroid()
        groupIos()
        withJvm()
        groupMacos()
        withJs()
//        withWasmJs()
        groupPhone()
        groupDesktop()
        groupJvmCommon()
        groupApple()
//        groupJsCommon()
    }
}


private fun KotlinHierarchyBuilder.groupPhone() {
    group("phone") {
        withAndroid()
        groupIos()
    }
}

private fun KotlinHierarchyBuilder.groupNonPhone() {
    group("nonPhone") {
//        withAndroid()
//        groupIos()
        withJvm()
        groupMacos()
        withJs()
        withWasmJs()
//        groupPhone()
        groupDesktop()
//        groupJvmCommon()
//        groupApple()
        groupJsCommon()
    }
}

private fun KotlinHierarchyBuilder.groupDesktop() {
    group("desktop") {
        groupMacos()
        withJvm()
    }
}

private fun KotlinHierarchyBuilder.groupNonDesktop() {
    group("nonDesktop") {
        withAndroid()
        groupIos()
//        withJvm()
//        groupMacos()
        withJs()
        withWasmJs()
        groupPhone()
//        groupDesktop()
//        groupJvmCommon()
//        groupApple()
        groupJsCommon()
    }
}

private fun KotlinHierarchyBuilder.groupJvmCommon() {
    group("jvmCommon") {
        withAndroid()
        withJvm()
    }
}

private fun KotlinHierarchyBuilder.groupNonJvmCommon() {
    group("nonJvmCommon") {
//        withAndroid()
        groupIos()
//        withJvm()
        groupMacos()
        withJs()
        withWasmJs()
//        groupPhone()
//        groupDesktop()
//        groupJvmCommon()
        groupApple()
        groupJsCommon()
    }
}

private fun KotlinHierarchyBuilder.groupApple() {
    group("apple") {
        groupIos()
        groupMacos()
    }
}

private fun KotlinHierarchyBuilder.groupNonApple() {
    group("nonApple") {
        withAndroid()
//        groupIos()
        withJvm()
//        groupMacos()
        withJs()
        withWasmJs()
//        groupPhone()
//        groupDesktop()
        groupJvmCommon()
//        groupApple()
        groupJsCommon()
    }
}

private fun KotlinHierarchyBuilder.groupJsCommon() {
    group("jsCommon") {
        withJs()
        withWasmJs()
    }
}

private fun KotlinHierarchyBuilder.groupNonJsCommon() {
    group("nonJsCommon") {
        withAndroid()
        groupIos()
        withJvm()
        groupMacos()
//        withJs()
//        withWasmJs()
        groupPhone()
        groupDesktop()
        groupJvmCommon()
        groupApple()
//        groupJsCommon()
    }
}


fun KotlinMultiplatformExtension.applyMyHierarchyTemplate() {
    applyHierarchyTemplate(hierarchyTemplate)
}
