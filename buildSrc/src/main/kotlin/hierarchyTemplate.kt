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

import com.android.build.api.dsl.KotlinMultiplatformAndroidCompilation
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
        groupJsCommon()
        groupNonJsCommon()
        groupJvmCommon()
        groupNonJvmCommon()
        groupNative()
        groupNonNative()
    }
}

// Define a helper function to be compatible with old and new Android targets
private fun KotlinHierarchyBuilder.withAndroid() {
    // Compatible with old androidTarget()
    withAndroidTarget()
    // Compatible with the new com.android.kotlin.multiplatform.library plugin, which creates targets usually named "android"
    // https://issuetracker.google.com/issues/442950553?pli=1
    // https://youtrack.jetbrains.com/issue/KT-80409
    // withAndroid()    // Will be introduced in version 9.1.0 of the Android Gradle plugin
    withCompilations { it is KotlinMultiplatformAndroidCompilation }
}

private fun KotlinHierarchyBuilder.groupNonAndroid() {
    group("nonAndroid") {
        withJvm()
        groupNative()
        withJs()
        withWasmJs()
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
        withJvm()
        groupJvmCommon()
        groupNative()
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
        groupNative()
        withJs()
        withWasmJs()
        groupJsCommon()
    }
}

private fun KotlinHierarchyBuilder.groupNative() {
    group("native") {
        withNative()

        group("apple") {
            withApple()

            group("ios") {
                withIos()
            }

            group("macos") {
                withMacos()
            }
        }
    }
}

private fun KotlinHierarchyBuilder.groupNonNative() {
    group("nonNative") {
        withAndroid()
        withJvm()
        withJs()
        withWasmJs()
        groupJvmCommon()
        groupJsCommon()
    }
}

fun KotlinMultiplatformExtension.applyMyHierarchyTemplate() {
    applyHierarchyTemplate(hierarchyTemplate)
}
