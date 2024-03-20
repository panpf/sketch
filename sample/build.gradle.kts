import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

kotlin {
    androidTarget {
        compilations.configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm("desktop") {
//        jvmToolchain(17)
//        withJava()
        compilations.configureEach {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    sourceSets {
        named("androidMain") {
            dependencies {
                implementation(project(":sketch-extensions"))
                implementation(project(":sketch-animated"))
                implementation(project(":sketch-animated-koralgif"))
                implementation(project(":sketch-okhttp"))
                implementation(project(":sketch-video"))
                implementation(project(":sketch-video-ffmpeg"))

                implementation(libs.kotlinx.serialization.json)

                implementation(libs.androidx.activity.compose)
//                implementation(libs.androidx.compose.animation)
//                implementation(libs.androidx.compose.foundation)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
//                implementation(libs.androidx.paging.compose)
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.constraintlayout)
                implementation(libs.androidx.core)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.androidx.multidex)
                implementation(libs.androidx.navigation.fragment)
                implementation(libs.androidx.navigation.ui)
//                implementation(libs.androidx.paging.common)
                implementation(libs.androidx.paging.runtime)
                implementation(libs.androidx.recyclerview)
                implementation(libs.androidx.swiperefreshlayout)

                implementation(libs.google.material)
                implementation(libs.panpf.assemblyadapter4.pager)
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
                implementation(libs.panpf.activitymonitor)
                implementation(libs.panpf.zoomimage.view)
                implementation(libs.tinypinyin)
                implementation(libs.okhttp3.logging)
            }
        }
        named("androidInstrumentedTest") {
            dependencies {
                implementation(project(":sketch-test"))
            }
        }
        named("commonMain") {
            dependencies {
                implementation(project(":sketch-compose"))
                implementation(project(":sketch-svg"))
                implementation(project(":sketch-resources"))
                implementation(project(":sketch-extensions-compose"))
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.uiTooling)
                implementation(compose.uiTooling.replace("ui-tooling", "ui-util"))
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.serialization.kotlinxJson)
                implementation(libs.cashapp.paging.compose.common)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenModel)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.androidx.datastore.core.okio)
                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.panpf.zoomimage.compose)
            }
        }
        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":sketch-okhttp"))
                implementation(project(":sketch-animated"))
                implementation(libs.harawata.appdirs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = property("GROUP").toString()
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

compose {
    kotlinCompilerPlugin = libs.jetbrains.compose.compiler.get().toString()
}

android {
    namespace = "com.github.panpf.sketch.sample"
    compileSdk = property("compileSdk").toString().toInt()

    defaultConfig {
        applicationId = "com.github.panpf.sketch3.sample"

        minSdk = property("minSdk").toString().toInt()
        targetSdk = property("targetSdk").toString().toInt()
        versionCode = property("versionCode").toString().toInt()
        versionName = property("versionName").toString()

        vectorDrawables.useSupportLibrary = true
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
        compose = true
    }

    // Set both the Java and Kotlin compilers to target Java 8.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.jetbrains.compose.compiler.get()
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