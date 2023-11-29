plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
    alias(libs.plugins.org.jetbrains.kotlin.parcelize)
//    id("kotlin-parcelize")
//    id("kotlinx-serialization")
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
}

dependencies {
    implementation(project(":sketch"))
    implementation(project(":sketch-compose"))
    implementation(project(":sketch-extensions"))
    implementation(project(":sketch-gif-movie"))
    implementation(project(":sketch-gif-koral"))
    implementation(project(":sketch-okhttp"))
    implementation(project(":sketch-svg"))
    implementation(project(":sketch-video"))
    implementation(project(":sketch-video-ffmpeg"))
//    implementation(project(":sketch-zoom"))
    implementation(project(":sketch-resources"))

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.bundles.androidx.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.multidex)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.bundles.androidx.paging)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.palette)

    implementation(libs.google.material)
    implementation(libs.bundles.panpf.assemblyadapter4)
    implementation(libs.bundles.panpf.tools4a)
    implementation(libs.bundles.panpf.tools4j)
    implementation(libs.panpf.tools4k)
    implementation(libs.panpf.activitymonitor)
    implementation(libs.panpf.zoomimage.compose.sketch) {
        exclude(group = "io.github.panpf.sketch3")
    }
    implementation(libs.panpf.zoomimage.view.sketch) {
        exclude(group = "io.github.panpf.sketch3")
    }
    implementation(libs.bundles.retrofit2)
    implementation(libs.tinypinyin)
    implementation(libs.okhttp3.logging)
    implementation(libs.mmkv)

    debugImplementation(libs.leakcanary)
}