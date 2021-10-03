plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = property("COMPILE_SDK_VERSION").toString().toInt()

    defaultConfig {
        applicationId = "me.panpf.sketch.sample.videothumbnail"

        minSdk = property("MIN_SDK_VERSION").toString().toInt()
        targetSdk = property("TARGET_SDK_VERSION").toString().toInt()
        versionCode = property("VERSION_CODE").toString().toInt()
        versionName = property("VERSION_NAME").toString()
    }

    val localProperties = `java.util`.Properties().apply {
        project.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
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
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig =
                if (jksFile != null && jksFile.exists()) signingConfigs.getByName("release") else signingConfig
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":sketch"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${property("KOTLIN_VERSION")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${property("KOTLINX_COROUTINES_ANDROID")}")

    implementation("androidx.core:core-ktx:${property("ANDROIDX_CORE_KTX")}")
    implementation("androidx.appcompat:appcompat:${property("ANDROIDX_APPCOMPAT")}")
    implementation("androidx.recyclerview:recyclerview:${property("ANDROIDX_RECYCLERVIEW")}")
    implementation("androidx.constraintlayout:constraintlayout:${property("ANDROIDX_CONSTRAINTLAYOUT")}")
    implementation("androidx.lifecycle:lifecycle-viewmodel:${property("ANDROIDX_LIFECYCLE")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${property("ANDROIDX_LIFECYCLE")}")
    implementation("androidx.paging:paging-common:${property("ANDROIDX_PAGING")}")
    implementation("androidx.paging:paging-runtime:${property("ANDROIDX_PAGING")}")

    implementation("io.github.panpf.assemblyadapter:assemblyadapter:${property("ASSEMBLY_ADAPTER_VERSION")}")
    implementation("io.github.panpf.assemblyadapter:assemblyadapter-ktx:${property("ASSEMBLY_ADAPTER_VERSION")}")
//    implementation("io.github.panpf.tools4a:tools4a:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4j:tools4j-date-ktx:${property("TOOLS4J")}")
    implementation("com.github.wseemann:FFmpegMediaMetadataRetriever:${property("FFMPEG_MEDIA_METADATA_RETRIEVER_VERSION")}")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:${property("LEAK_CANARY_ANDROID_VERSION")}")
    debugImplementation("com.squareup.leakcanary:leakcanary-support-fragment:${property("LEAK_CANARY_ANDROID_VERSION")}")
    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${property("LEAK_CANARY_ANDROID_VERSION")}")
    testImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${property("LEAK_CANARY_ANDROID_VERSION")}")
}