import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlinx.atomicfu")
}

kotlin {
    applyMyHierarchyTemplate()

    androidKmpLibrary(nameSpace = "com.github.panpf.sketch.sample.compose")

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

    jvm("desktop")

    js {
        outputModuleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.internal.images)
            api(projects.sketchAnimatedGif)
            api(projects.sketchAnimatedWebp)
            api(projects.sketchBlurhash)
            api(projects.sketchComposeKoin)
            api(projects.sketchComposeResources)
            api(projects.sketchExtensionsCompose)
            api(projects.sketchExtensionsComposeResources)
            api(projects.sketchHttpKtor3)
            api(projects.sketchSvg)
            api(libs.jetbrains.compose.components.resources)
            api(libs.jetbrains.compose.material)    // pull refresh
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.material.icons.core)
            api(libs.jetbrains.compose.ui.tooling.preview)
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
            api(libs.koin.compose.viewmodel.navigation)
            api(libs.ktor3.client.contentNegotiation)
            api(libs.ktor3.serialization.kotlinxJson)
            api(libs.multiplatformsettings)
            api(libs.panpf.zoomimage.compose)
            api(libs.voyager.navigator)
            api(libs.voyager.transitions)
        }
        androidMain.dependencies {
            api(projects.sketchAnimatedGifKoral)
            api(projects.sketchAnimatedHeif)
            api(projects.sketchExtensionsAppicon)
            api(projects.sketchExtensionsApkicon)
            api(projects.sketchVideo)
//            api(projects.sketchVideoFfmpeg)   // https://github.com/wseemann/FFmpegMediaMetadataRetriever/issues/294
            api(libs.androidx.activity.compose)
            api(libs.androidx.appcompat)
            api(libs.androidx.constraintlayout.compose)
            api(libs.androidx.lifecycle.viewmodel.compose)
            api(libs.androidx.lifecycle.runtime)
            api(libs.androidx.navigation.compose)
            api(libs.androidx.navigation.ui)
            api(libs.koin.android)
            api(libs.kotlinx.serialization.json)
            api(libs.moko.permissions)
            api(libs.moko.permissions.storage)
            api(libs.panpf.tools4a.activity)
            api(libs.panpf.tools4a.device)
            api(libs.panpf.tools4a.dimen)
            api(libs.panpf.tools4a.display)
            api(libs.panpf.tools4a.fileprovider)
            api(libs.panpf.tools4a.network)
            api(libs.panpf.tools4a.toast)
            api(libs.panpf.tools4j.date)
            api(libs.panpf.tools4j.math)
            api(libs.panpf.tools4j.io)
            api(libs.panpf.tools4j.security)
            api(libs.panpf.tools4k)
            api(libs.penfeizhou.animation.awebp)
        }
        desktopMain.dependencies {
            api(compose.desktop.currentOs)
        }
        jvmCommonMain.dependencies {
            api(projects.sketchHttpHurl)
            api(projects.sketchHttpOkhttp)
        }
        iosMain {
            // It has been configured in the internal:images module, but it is still inaccessible in the sample module. This may be a bug of kmp.
            resources.srcDirs("../../internal/images/src/iosMain/resources")
            dependencies {
                api(libs.moko.permissions)
                api(libs.moko.permissions.storage)
            }
        }
        nonJsCommonMain.dependencies {
            api(libs.multiplatform.paging)
        }

        commonTest.dependencies {
            implementation(projects.internal.test)
            implementation(projects.internal.testSingleton)
        }
    }
}

compose.resources {
    packageOfResClass = "com.github.panpf.sketch.sample"
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