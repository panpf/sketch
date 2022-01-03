import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = property("COMPILE_SDK").toString().toInt()

    defaultConfig {
        applicationId = "com.github.panpf.sketch3.sample"

        minSdk = property("MIN_SDK").toString().toInt()
        targetSdk = property("TARGET_SDK").toString().toInt()
        versionCode = property("VERSION_CODE").toString().toInt()
        versionName = "${property("VERSION_NAME")}.${getGitVersion()}"
    }

    val localProperties = Properties().apply {
        project.file("local.properties").takeIf { it.exists() }
            ?.inputStream()
            ?.use { load(it) }
    }.takeIf { !it.isEmpty }
    val jksFile = localProperties?.getProperty("sample.storeFile")?.let { file(it) }

    signingConfigs {
        create("release") {
            storeFile = jksFile
            storePassword = localProperties?.getProperty("sample.storePassword")
            keyAlias = localProperties?.getProperty("sample.keyAlias")
            keyPassword = localProperties?.getProperty("sample.keyPassword")
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig =
                if (jksFile != null && jksFile.exists()) signingConfigs.getByName("release") else signingConfig
            multiDexEnabled = true
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig =
                if (jksFile != null && jksFile.exists()) signingConfigs.getByName("release") else signingConfig
        }
    }

    flavorDimensions.add("default")

    androidResources {
        noCompress("bmp")
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":sketch3"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${property("KOTLIN")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${property("KOTLINX_COROUTINES_ANDROID")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("KOTLINX_SERIALIZATION_JSON")}")

    implementation("androidx.appcompat:appcompat:${property("ANDROIDX_APPCOMPAT")}")
    implementation("androidx.constraintlayout:constraintlayout:${property("ANDROIDX_CONSTRAINTLAYOUT")}")
    implementation("androidx.core:core-ktx:${property("ANDROIDX_CORE")}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${property("ANDROIDX_LIFECYCLE")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${property("ANDROIDX_LIFECYCLE")}")
    implementation("androidx.multidex:multidex:${property("MULTIDEX")}")
    implementation("androidx.navigation:navigation-fragment-ktx:${property("ANDROIDX_NAVIGATION")}")
    implementation("androidx.navigation:navigation-ui-ktx:${property("ANDROIDX_NAVIGATION")}")
    implementation("androidx.paging:paging-common:${property("ANDROIDX_PAGING")}")
    implementation("androidx.paging:paging-runtime:${property("ANDROIDX_PAGING")}")
    implementation("androidx.recyclerview:recyclerview:${property("ANDROIDX_RECYCLERVIEW")}")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:${property("ANDROIDX_SWIPEREFRESHLAYOUT")}")

    implementation("com.google.android:flexbox:${property("FLEXBOX")}")
    implementation("com.google.android.material:material:${property("GOOGLE_MATERIAL")}")
    implementation("io.github.panpf.assemblyadapter:assemblyadapter:${property("ASSEMBLY_ADAPTER")}")
    implementation("io.github.panpf.assemblyadapter:assemblyadapter-ktx:${property("ASSEMBLY_ADAPTER")}")
    implementation("io.github.panpf.assemblyadapter4:assemblyadapter-recycler:${property("ASSEMBLY_ADAPTER_4")}")
    implementation("io.github.panpf.assemblyadapter4:assemblyadapter-recycler-paging:${property("ASSEMBLY_ADAPTER_4")}")
    implementation("io.github.panpf.assemblyadapter4:assemblyadapter-pager2:${property("ASSEMBLY_ADAPTER_4")}")
    implementation("io.github.panpf.tools4a:tools4a-activity-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4a:tools4a-device-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4a:tools4a-display-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4a:tools4a-dimen-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4a:tools4a-fileprovider-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4a:tools4a-toast-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4j:tools4j-math-ktx:${property("TOOLS4J")}")
    implementation("io.github.panpf.tools4j:tools4j-date-ktx:${property("TOOLS4J")}")
    implementation("io.github.panpf.tools4k:tools4k:${property("TOOLS4K")}")
    implementation("com.github.promeg:tinypinyin:${property("TINYPINYIN")}")
    implementation("io.github.panpf.liveevent:liveevent:${property("LIVEEVENT")}")
    implementation("com.squareup.retrofit2:retrofit:${property("RETROFIT")}")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${property("RETROFIT2_KOTLINX_SERIALIZATION_CONVERTER")}")
    implementation("com.github.wseemann:FFmpegMediaMetadataRetriever:${property("FFMPEG_MEDIA_METADATA_RETRIEVER")}")

    implementation(files("libs/bugly_1.2.3.8__release.jar"))

    debugImplementation("com.squareup.leakcanary:leakcanary-android:${property("LEAK_CANARY")}")
}

fun getGitVersion(): String =
    Runtime.getRuntime().exec("git rev-parse --short HEAD").inputStream.use {
        it.bufferedReader().readText().trim()
    }