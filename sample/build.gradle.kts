import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlinx.atomicfu")
    id("androidx.navigation.safeargs.kotlin")      // Must be after kotlin plugin
}

kotlin {
    applyMyHierarchyTemplate()

    androidTarget()

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    js {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.internal.images)
            implementation(projects.sketchAnimatedGif)
            implementation(projects.sketchAnimatedWebp)
            implementation(projects.sketchBlurhash)
            implementation(projects.sketchComposeKoin)
            implementation(projects.sketchComposeResources)
            implementation(projects.sketchExtensionsCompose)
            implementation(projects.sketchExtensionsComposeResources)
            implementation(projects.sketchHttpKtor3)
            implementation(projects.sketchSvg)
            implementation(compose.components.resources)
            implementation(compose.material)    // pull refresh
            implementation(compose.material3)
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.ktor3.client.contentNegotiation)
            implementation(libs.ktor3.serialization.kotlinxJson)
            implementation(libs.multiplatformsettings)
            implementation(libs.panpf.zoomimage.compose)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
        }
        androidMain.dependencies {
            implementation(projects.sketchAnimatedGifKoral)
            implementation(projects.sketchAnimatedHeif)
            implementation(projects.sketchExtensionsAppicon)
            implementation(projects.sketchExtensionsApkicon)
            implementation(projects.sketchExtensionsView)
            implementation(projects.sketchVideo)
            implementation(projects.sketchVideoFfmpeg)
            implementation(projects.sketchViewKoin)
            implementation(compose.preview) // Only available on Android and desktop platforms
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.constraintlayout)
            implementation(libs.androidx.constraintlayout.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.navigation.fragment)
            implementation(libs.androidx.navigation.ui)
            implementation(libs.androidx.recyclerview)
            implementation(libs.androidx.swiperefreshlayout)
            implementation(libs.google.material)
            implementation(libs.google.flexbox)
            implementation(libs.koin.android)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.moko.permissions)
            implementation(libs.moko.permissions.storage)
            implementation(libs.panpf.assemblyadapter4.pager2)
            implementation(libs.panpf.assemblyadapter4.recycler)
            implementation(libs.panpf.assemblyadapter4.recycler.paging)
            implementation(libs.panpf.tools4a.activity)
            implementation(libs.panpf.tools4a.device)
            implementation(libs.panpf.tools4a.dimen)
            implementation(libs.panpf.tools4a.display)
            implementation(libs.panpf.tools4a.fileprovider)
            implementation(libs.panpf.tools4a.network)
            implementation(libs.panpf.tools4a.toast)
            implementation(libs.panpf.tools4j.date)
            implementation(libs.panpf.tools4j.math)
            implementation(libs.panpf.tools4j.io)
            implementation(libs.panpf.tools4j.security)
            implementation(libs.panpf.tools4k)
            implementation(libs.panpf.zoomimage.view)
            implementation(libs.penfeizhou.animation.awebp)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.preview) // Only available on Android and desktop platforms
        }
        jvmCommonMain.dependencies {
            implementation(projects.sketchHttpHurl)
            implementation(projects.sketchHttpOkhttp)
        }
        iosMain {
            // It has been configured in the internal:images module, but it is still inaccessible in the sample module.
            // This may be a bug of kmp.
            resources.srcDirs("../internal/images/files")
            dependencies {
                implementation(projects.sketchVideo)
                implementation(libs.moko.permissions)
                implementation(libs.moko.permissions.storage)
            }
        }
        nonJsCommonMain.dependencies {
            implementation(libs.multiplatform.paging)
        }
        wasmJsMain.dependencies {
            // https://youtrack.jetbrains.com/issue/KTOR-7934/JS-WASM-fails-with-IllegalStateException-Content-Length-mismatch-on-requesting-gzipped-content
            implementation(libs.ktor31.client.wasmJs)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}

compose.resources {
    packageOfResClass = "com.github.panpf.sketch.sample.resources"
}

val appId = "com.github.panpf.sketch4.sample"
val appName = "Sketch4 Sample"
compose.desktop {
    application {
        mainClass = "com.github.panpf.sketch.sample.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appName
            packageVersion = convertDesktopPackageVersion(property("versionName").toString())
            vendor = "panpfpanpf@outlook.com"
            description = "Sketch4 Image Loader Library Sample App"
            macOS {
                bundleID = appId
                iconFile.set(project.file("icons/icon-macos.icns"))
            }
            windows {
                iconFile.set(project.file("icons/icon-windows.ico"))
            }
            linux {
                iconFile.set(project.file("icons/icon-linux.png"))
            }
            modules(
                "jdk.unsupported",  // 'sun/misc/Unsafe' error
                "java.net.http",    // 'java/net/http/HttpClient$Version ' error
            )
        }
        buildTypes.release.proguard {
            obfuscate.set(true) // Obfuscate the code
            optimize.set(true) // proguard optimization, enabled by default
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}

androidApplication(
    nameSpace = appId.replace("sketch4", "sketch"),
    applicationId = appId
) {
    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${property("versionName").toString()}\"")
        buildConfigField("int", "VERSION_CODE", property("versionCode").toString())
    }
    signingConfigs {
        create("sample") {
            storeFile = project.file("sample.keystore")
            storePassword = "B027HHiiqKOMYesQ"
            keyAlias = "panpf-sample"
            keyPassword = "B027HHiiqKOMYesQ"
        }
    }
    buildTypes {
        debug {
            multiDexEnabled = true
            signingConfig = signingConfigs.getByName("sample")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("sample")
        }
    }

    flavorDimensions.add("default")

    androidResources {
        noCompress.add("bmp")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this
            if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                output.outputFileName =
                    "sketch-sample-${variant.name}-${variant.versionName}.apk"
            }
        }
    }

    dependencies {
        debugImplementation(libs.leakcanary)
    }
}

// https://youtrack.jetbrains.com/issue/KT-56025
afterEvaluate {
    tasks {
        val configureJs: Task.() -> Unit = {
            dependsOn(named("jsDevelopmentExecutableCompileSync"))
            dependsOn(named("jsProductionExecutableCompileSync"))
            dependsOn(named("jsTestTestDevelopmentExecutableCompileSync"))
        }
        named("jsBrowserProductionWebpack").configure(configureJs)
    }
}
// https://youtrack.jetbrains.com/issue/KT-56025
afterEvaluate {
    tasks {
        val configureWasmJs: Task.() -> Unit = {
            dependsOn(named("wasmJsDevelopmentExecutableCompileSync"))
            dependsOn(named("wasmJsProductionExecutableCompileSync"))
            dependsOn(named("wasmJsTestTestDevelopmentExecutableCompileSync"))
        }
        named("wasmJsBrowserProductionWebpack").configure(configureWasmJs)
    }
}

// https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html
// The current 1.6.1 version only supports the use of compose resources in the commonMain source set of the Feiku module.
// The files of the images module can only be added to the js module in this way.
tasks.register<Copy>("copyImagesToJsProcessedResources") {
    from(project(":internal:images").file("files"))
    into(project(":sample").file("build/processedResources/js/main/files"))
}
tasks.named("jsProcessResources") {
    dependsOn("copyImagesToJsProcessedResources")
}
tasks.register<Copy>("copyImagesToWasmJsProcessedResources") {
    from(project(":internal:images").file("files"))
    into(project(":sample").file("build/processedResources/wasmJs/main/files"))
}
tasks.named("wasmJsProcessResources") {
    dependsOn("copyImagesToWasmJsProcessedResources")
}

/**
 * '1.2.0' -> '1.2.0099'
 * '1.2.1' -> '1.2.0199'
 * '1.2.21' -> '1.2.2199'
 * '1.2.1-alpha01' -> '1.2.0101'
 * '1.2.1-alpha01' -> '1.2.0101'
 * '1.2.1-beta01' -> '1.2.0131'
 * '1.2.1-rc01' -> '1.2.0161'
 */
private fun convertDesktopPackageVersion(version: String): String {
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