import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlinx-atomicfu")
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
//                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
//                    static = (static ?: mutableListOf()).apply {
//                        // Serve sources to debug inside browser
//                        add(project.projectDir.path)
//                    }
//                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.internal.images)
            implementation(projects.sketchAnimated)
            implementation(projects.sketchCompose)
            implementation(projects.sketchComposeResources)
            implementation(projects.sketchExtensionsCompose)
            implementation(projects.sketchHttpKtor)
            implementation(projects.sketchSvg)
            implementation(compose.components.resources)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinxJson)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.transitions)
        }
        androidMain.dependencies {
            implementation(projects.sketchAnimatedKoralgif)
            implementation(projects.sketchExtensionsView)
            implementation(projects.sketchVideo)
            implementation(projects.sketchVideoFfmpeg)
            implementation(projects.sketchView)
            implementation(compose.preview)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.constraintlayout)
            implementation(libs.androidx.constraintlayout.compose)
            implementation(libs.androidx.core)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.androidx.multidex)
            implementation(libs.androidx.navigation.fragment)
            implementation(libs.androidx.navigation.ui)
            implementation(libs.androidx.recyclerview)
            implementation(libs.androidx.swiperefreshlayout)
            implementation(libs.google.material)
            implementation(libs.panpf.assemblyadapter4.pager2)
            implementation(libs.panpf.assemblyadapter4.recycler)
            implementation(libs.panpf.assemblyadapter4.recycler.paging)
            implementation(libs.panpf.tools4a.activity)
            implementation(libs.panpf.tools4a.device)
            implementation(libs.panpf.tools4a.display)
            implementation(libs.panpf.tools4a.dimen)
            implementation(libs.panpf.tools4a.fileprovider)
            implementation(libs.panpf.tools4a.network)
            implementation(libs.panpf.tools4a.toast)
            implementation(libs.panpf.tools4j.date)
            implementation(libs.panpf.tools4j.math)
            implementation(libs.panpf.tools4j.io)
            implementation(libs.panpf.tools4j.security)
            implementation(libs.panpf.tools4k)
            implementation(libs.panpf.zoomimage.view)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.preview)
        }
        jvmCommonMain.dependencies {
            implementation(projects.sketchHttpOkhttp)
            implementation(libs.panpf.zoomimage.compose)
        }
        iosMain {
            // It will not be transferred automatically and needs to be actively configured.. This may be a bug of kmp.
            resources.srcDirs("../internal/images/files")
        }
        nonJsCommonMain.dependencies {
            implementation(libs.cashapp.paging.compose.common)
            implementation(libs.androidx.datastore.core.okio)
            implementation(libs.androidx.datastore.preferences.core)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.contentNegotiation.wasm)
            implementation(libs.ktor.serialization.kotlinxJson.wasm)
        }

        commonTest.dependencies {
            implementation(projects.internal.testUtils)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.panpf.sketch.sample.MainApp2Kt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.github.panpf.sketch.sample"
            packageVersion = property("versionName").toString().let {
                if (it.contains("-")) {
                    it.substring(0, it.indexOf("-"))
                } else {
                    it
                }
            }
        }
    }
}

androidApplication(
    nameSpace = "com.github.panpf.sketch.sample",
    applicationId = "com.github.panpf.sketch4.sample"
) {
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
            isShrinkResources = true
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