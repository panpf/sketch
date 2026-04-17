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

kmpAndroidLibrary(nameSpace = "com.github.panpf.sketch.sample.compose")

kotlin {
    applyMyHierarchyTemplate()

    listOf(
//        iosX64(), // androidx.paging doesn't support iosX64 since 3.4.0
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
        browser()
        binaries.executable()
        binaries.library()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
        binaries.library()
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
            api(libs.androidx.paging.compose)
            api(libs.jetbrains.compose.components.resources)
            api(libs.jetbrains.compose.material)    // pull refresh
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.material.icons.core)
            api(libs.jetbrains.compose.material3.adaptiveNavigation3)
            api(libs.jetbrains.compose.ui.tooling.preview)  // 'libs.jetbrains.compose.ui.tooling' must also be configured, otherwise the preview will report an error: java.lang.ClassNotFoundException: androidx.compose.ui.tooling.PreviewParameterProvider
            api(libs.jetbrains.lifecycle.viewmodelNavigation3)
            api(libs.jetbrains.navigation3.ui)
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
            api(libs.koin.compose.viewmodel.navigation)
            api(libs.ktor3.client.contentNegotiation)
            api(libs.ktor3.serialization.kotlinxJson)
            api(libs.multiplatformsettings)
            api(libs.panpf.zoomimage.compose)
        }
        androidMain.dependencies {
            api(projects.sketchAnimatedGifKoral)
            api(projects.sketchAnimatedHeif)
            api(projects.sketchExtensionsAppicon)
            api(projects.sketchExtensionsApkicon)
            api(projects.sketchVideo)
            api(projects.sketchVideoFfmpeg)   // https://github.com/wseemann/FFmpegMediaMetadataRetriever/issues/294
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
            api(libs.moko.permissions.gallery)
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
                api(projects.sketchVideo)
                api(libs.moko.permissions)
                api(libs.moko.permissions.storage)
                api(libs.moko.permissions.gallery)
            }
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

dependencies {
    androidRuntimeClasspath(libs.jetbrains.compose.ui.tooling)  // For compose preview
}