import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = property("COMPILE_SDK_VERSION").toString().toInt()

    defaultConfig {
        applicationId = "me.panpf.sketch.sample"

        minSdk = property("MIN_SDK_VERSION").toString().toInt()
        targetSdk = property("TARGET_SDK_VERSION").toString().toInt()
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

    productFlavors {
        create("normal") {
            dimension = "default"
        }

        create("lollipop") {
            dimension = "default"
            minSdk = 21
        }

        // 为了测试没有导入 sketch-gif 时是否可以正常运行
        create("normalNoGifLib") {
            dimension = "default"
        }

        // 为了测试没有导入 sketch-gif 时是否可以正常运行
        create("lollipopNoGifLib") {
            dimension = "default"
            minSdk = 21
        }
    }

    androidResources {
        noCompress("bmp")
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":sketch"))
    implementation(project(":sketch-zoom"))
    add("normalImplementation", project(":sketch-gif"))
    add("lollipopImplementation", project(":sketch-gif"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${property("KOTLIN_VERSION")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${property("KOTLINX_COROUTINES_ANDROID")}")
    implementation("androidx.multidex:multidex:${property("MULTIDEX")}")

    implementation("androidx.appcompat:appcompat:${property("ANDROIDX_APPCOMPAT")}")
    implementation("androidx.recyclerview:recyclerview:${property("ANDROIDX_RECYCLERVIEW")}")
    implementation("androidx.constraintlayout:constraintlayout:${property("ANDROIDX_CONSTRAINTLAYOUT")}")
    implementation("androidx.core:core-ktx:${property("ANDROIDX_CORE_KTX")}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${property("ANDROIDX_LIFECYCLE")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${property("ANDROIDX_LIFECYCLE")}")
    implementation("androidx.paging:paging-common:${property("ANDROIDX_PAGING")}")
    implementation("androidx.paging:paging-runtime:${property("ANDROIDX_PAGING")}")
    implementation("androidx.navigation:navigation-fragment-ktx:${property("ANDROIDX_NAVIGATION")}")
    implementation("androidx.navigation:navigation-ui-ktx:${property("ANDROIDX_NAVIGATION")}")

    implementation("com.google.android:flexbox:${property("FLEXBOX")}")
    implementation("com.google.android.material:material:${property("GOOGLE_MATERIAL")}")
    implementation("io.github.panpf.pagerindicator:pagerindicator:${property("PAGER_INDICATOR")}")
    implementation("io.github.panpf.assemblyadapter:assemblyadapter:${property("ASSEMBLY_ADAPTER_VERSION")}")
    implementation("io.github.panpf.assemblyadapter:assemblyadapter-ktx:${property("ASSEMBLY_ADAPTER_VERSION")}")
    implementation("io.github.panpf.tools4a:tools4a-display-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4a:tools4a-device-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4a:tools4a-toast-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4a:tools4a-dimen-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4a:tools4a-args-ktx:${property("TOOLS4A")}")
    implementation("io.github.panpf.tools4j:tools4j-math-ktx:${property("TOOLS4J")}")
    implementation("com.github.promeg:tinypinyin:${property("TINYPINYIN")}")
    implementation("io.github.panpf.liveevent:liveevent:${property("LIVEEVENT")}")
    implementation("org.greenrobot:eventbus:${property("EVENT_BUS_VERSION")}")
    implementation("com.squareup.retrofit2:retrofit:${property("RETROFIT_VERSION")}")
    implementation("com.squareup.retrofit2:converter-gson:${property("RETROFIT_VERSION")}")

    implementation(files("libs/bugly_1.2.3.8__release.jar"))

    debugImplementation("com.squareup.leakcanary:leakcanary-android:${property("LEAK_CANARY_ANDROID_VERSION")}")
    debugImplementation("com.squareup.leakcanary:leakcanary-support-fragment:${property("LEAK_CANARY_ANDROID_VERSION")}")
    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${property("LEAK_CANARY_ANDROID_VERSION")}")
    testImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${property("LEAK_CANARY_ANDROID_VERSION")}")
}

fun getGitVersion(): String =
    Runtime.getRuntime().exec("git rev-parse --short HEAD").inputStream.use {
        it.bufferedReader().readText().trim()
    }