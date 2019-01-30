import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

val localProperties = Properties().apply { project.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) } }.takeIf { !it.isEmpty }
val jksFile = localProperties?.getProperty("sample.storeFile")?.let { file(it) } ?: null

android {
    compileSdkVersion(property("COMPILE_SDK_VERSION").toString().toInt())

    defaultConfig {
        applicationId = "me.panpf.sketch.sample.videothumbnail"

        minSdkVersion(property("MIN_SDK_VERSION").toString().toInt())
        targetSdkVersion(property("TARGET_SDK_VERSION").toString().toInt())
        versionCode = property("VERSION_CODE").toString().toInt()
        versionName = property("VERSION_NAME").toString()

        ndk {
            abiFilters("armeabi", "x86")
        }
    }

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
            signingConfig = if (jksFile != null && jksFile.exists()) signingConfigs.getByName("release") else signingConfig
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = if (jksFile != null && jksFile.exists()) signingConfigs.getByName("release") else signingConfig
        }
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${property("KOTLIN_VERSION")}")
    implementation("androidx.core:core-ktx:${property("ANDROIDX_CORE_KTX")}")

    implementation(project(":sketch"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${property("KOTLIN_VERSION")}")
    implementation("androidx.core:core-ktx:${property("ANDROIDX_CORE_KTX")}")

    implementation("androidx.appcompat:appcompat:${property("ANDROIDX_APPCOMPAT")}")
    implementation("androidx.recyclerview:recyclerview:${property("ANDROIDX_RECYCLERVIEW")}")
    implementation("androidx.constraintlayout:constraintlayout:${property("ANDROIDX_CONSTRAINTLAYOUT")}")

    implementation("androidx.lifecycle:lifecycle-extensions:${property("ANDROIDX_LIFECYCLE")}")
    implementation("androidx.lifecycle:lifecycle-viewmodel:${property("ANDROIDX_LIFECYCLE")}")
    implementation("androidx.lifecycle:lifecycle-livedata:${property("ANDROIDX_LIFECYCLE")}")
    kapt("androidx.lifecycle:lifecycle-compiler:${property("ANDROIDX_LIFECYCLE")}")
    implementation("androidx.paging:paging-runtime:${property("ANDROIDX_PAGING")}")

    implementation("me.panpf:assembly-adapter:${property("ASSEMBLY_ADAPTER_VERSION")}")
    implementation("me.panpf:assembly-adapter-ktx:${property("ASSEMBLY_ADAPTER_VERSION")}")
    implementation("me.panpf:assembly-paged-list-adapter:${property("ASSEMBLY_ADAPTER_VERSION")}")
    implementation("me.panpf:androidx-kt:${property("PANPF_ANDROIDX")}")
    implementation("me.panpf:androidx-kt-arch:${property("PANPF_ANDROIDX")}")

    implementation("com.github.wseemann:FFmpegMediaMetadataRetriever:${property("FFMPEG_MEDIA_METADATA_RETRIEVER_VERSION")}")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:${property("LEAK_CANARY_ANDROID_VERSION")}")
    debugImplementation("com.squareup.leakcanary:leakcanary-support-fragment:${property("LEAK_CANARY_ANDROID_VERSION")}")
    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${property("LEAK_CANARY_ANDROID_VERSION")}")
    testImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${property("LEAK_CANARY_ANDROID_VERSION")}")
}
