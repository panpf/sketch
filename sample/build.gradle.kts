plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
    alias(libs.plugins.org.jetbrains.kotlin.parcelize)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
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
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs =
            freeCompilerArgs + "-P" + "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
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
}

dependencies {
    implementation(project(":sketch"))
    implementation(project(":sketch-compose"))
    implementation(project(":sketch-extensions"))
    implementation(project(":sketch-extensions-compose"))
    implementation(project(":sketch-gif-movie"))
    implementation(project(":sketch-gif-koral"))
    implementation(project(":sketch-okhttp"))
    implementation(project(":sketch-svg"))
    implementation(project(":sketch-video"))
    implementation(project(":sketch-video-ffmpeg"))
    implementation(project(":sketch-resources"))

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.palette)

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
    implementation(libs.panpf.zoomimage.compose)
    implementation(libs.panpf.zoomimage.view)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.kotlinxSerializationConverter)
    implementation(libs.tinypinyin)
    implementation(libs.okhttp3.logging)

    debugImplementation(libs.leakcanary)
}