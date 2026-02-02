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
import org.gradle.api.Project

/**
 * A list of all public modules in the project.
 *
 * Synced with settings.gradle.kts include list.
 */
val publicModules = setOf(
    "sketch-animated-core",
    "sketch-animated-gif",
    "sketch-animated-gif-koral",
    "sketch-animated-heif",
    "sketch-animated-webp",
    "sketch-blurhash",
    "sketch-compose",
    "sketch-compose-core",
    "sketch-compose-koin",
    "sketch-compose-resources",
    "sketch-core",
    "sketch-extensions-appicon",
    "sketch-extensions-apkicon",
    "sketch-extensions-core",
    "sketch-extensions-compose",
    "sketch-extensions-compose-resources",
    "sketch-extensions-view",
    "sketch-extensions-viewability",
    "sketch-http",
    "sketch-http-core",
    "sketch-http-hurl",
    "sketch-http-ktor2",
    "sketch-http-ktor2-core",
    "sketch-http-ktor3",
    "sketch-http-ktor3-core",
    "sketch-http-okhttp",
    "sketch-koin",
    "sketch-singleton",
    "sketch-svg",
    "sketch-video",
    "sketch-video-core",
    "sketch-video-ffmpeg",
    "sketch-view",
    "sketch-view-core",
    "sketch-view-koin",
)

val Project.minSdk: Int
    get() = intProperty("minSdk")

val Project.targetSdk: Int
    get() = intProperty("targetSdk")

val Project.compileSdk: Int
    get() = intProperty("compileSdk")

//val Project.groupId: String
//    get() = stringProperty("POM_GROUP_ID")

val Project.versionName: String
    get() = stringProperty("versionName")

val Project.versionCode: Int
    get() = intProperty("versionCode")

private fun Project.intProperty(
    name: String,
    default: () -> Int = { error("unknown property: $name") },
): Int = (properties[name] as String?)?.toInt() ?: default()

private fun Project.stringProperty(
    name: String,
    default: () -> String = { error("unknown property: $name") },
): String = (properties[name] as String?) ?: default()

private fun Project.booleanProperty(
    name: String,
    default: () -> Boolean = { error("unknown property: $name") },
): Boolean = (properties[name] as String?)?.toBooleanStrict() ?: default()

/**
 * '1.2.0' -> '1.2.0099'
 * '1.2.1' -> '1.2.0199'
 * '1.2.21' -> '1.2.2199'
 * '1.2.1-alpha01' -> '1.2.0101'
 * '1.2.1-alpha01' -> '1.2.0101'
 * '1.2.1-beta01' -> '1.2.0131'
 * '1.2.1-rc01' -> '1.2.0161'
 */
fun convertDesktopPackageVersion(version: String): String {
    val versionItems = version.split("-")
    val (major, preRelease) = when (versionItems.size) {
        2 -> versionItems[0] to versionItems[1]
        1 -> versionItems[0] to null
        else -> throw IllegalArgumentException("The version is invalid, version: $version")
    }
    val majorItems = major.split(".")
    require(majorItems.size == 3) {
        "The major part of the version string must have three parts, but was: $version"
    }
    val patch = majorItems[2].toIntOrNull()
        ?: throw IllegalArgumentException("The patch part of version is invalid. version: $version")
    require(patch < 100) {
        "The patch part of the version string must be less than 100, but was: $version"
    }

    val finalPreReleaseNumberFormatted = if (preRelease != null) {
        val preReleaseRules = listOf(
            "alpha" to 0,
            "beta" to 30,
            "rc" to 60
        )
        val preReleaseRule = preReleaseRules.find { preRelease.startsWith(it.first) }
            ?: throw IllegalArgumentException("The pre-release part of the version string must start with 'alpha', 'beta' or 'rc', but was: $version")
        val preReleaseNumber = preRelease.replace(preReleaseRule.first, "").toIntOrNull()
            ?: throw IllegalArgumentException("The pre-release part of the version string must start with 'alpha', 'beta' or 'rc', but was: $version")
        require(preReleaseNumber < 30) {
            "The pre-release number must be less than 30, but was: $version"
        }
        val finalPreReleaseNumber = preReleaseRule.second + preReleaseNumber
        String.format("%02d", finalPreReleaseNumber)
    } else {
        "99"
    }
    val finalPatch = String.format("%02d", patch)
    val newPatch = "${finalPatch}${finalPreReleaseNumberFormatted}"
    val newVersion = listOf(
        majorItems[0],  // Major
        majorItems[1],  // Minor
        newPatch       // Patch with pre-release number
    ).joinToString(".")
    return newVersion
}