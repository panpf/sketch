import java.util.*

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
        applicationId = "me.panpf.sketch.sample"

        minSdkVersion(property("MIN_SDK_VERSION").toString().toInt())
        targetSdkVersion(property("TARGET_SDK_VERSION").toString().toInt())
        versionCode = property("VERSION_CODE").toString().toInt()
        versionName = "${property("VERSION_NAME")}.${getGitVersion()}"
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
            multiDexEnabled = true
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = if (jksFile != null && jksFile.exists()) signingConfigs.getByName("release") else signingConfig
        }
    }

    flavorDimensions("default")

    productFlavors {
        create("normal") {
            setDimension("default")
        }

        create("lollipop") {
            setDimension("default")
            minSdkVersion(21)
        }

        // 为了测试没有导入 sketch-gif 时是否可以正常运行
        create("normalNogiflib") {
            setDimension("default")
        }

        // 为了测试没有导入 sketch-gif 时是否可以正常运行
        create("lollipopNogiflib") {
            setDimension("default")
            minSdkVersion(21)
        }
    }

    lintOptions {
        isAbortOnError = false
    }

    aaptOptions {
        noCompress("bmp")
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${property("KOTLIN_VERSION")}")
//    implementation("org.jetbrains.kotlin:kotlin-android-extensions:${property("KOTLIN_VERSION")}")
    implementation("androidx.multidex:multidex:${property("MULTIDEX")}")

    implementation("androidx.appcompat:appcompat:${property("ANDROIDX_APPCOMPAT")}")
    implementation("androidx.recyclerview:recyclerview:${property("ANDROIDX_RECYCLERVIEW")}")
    implementation("androidx.constraintlayout:constraintlayout:${property("ANDROIDX_CONSTRAINTLAYOUT")}")
    implementation("androidx.core:core-ktx:${property("ANDROIDX_CORE_KTX")}")

    implementation("me.panpf:pager-indicator:${property("PAGER_INDICATOR")}")
    implementation("me.panpf:assembly-adapter:${property("ASSEMBLY_ADAPTER_VERSION")}")
    implementation("me.panpf:assembly-adapter-ktx:${property("ASSEMBLY_ADAPTER_VERSION")}")
    implementation("me.panpf:androidx-kt:${property("PANPF_ANDROIDX")}")
    implementation("me.panpf:androidx-kt-arch:${property("PANPF_ANDROIDX")}")

    implementation(project(":sketch"))
    add("normalImplementation", project(":sketch-gif"))
    add("lollipopImplementation", project(":sketch-gif"))

    implementation(files("libs/bugly_1.2.3.8__release.jar"))
    implementation(files("libs/pinyin4j-2.5.0.jar"))

    debugImplementation("com.squareup.leakcanary:leakcanary-android:${property("LEAK_CANARY_ANDROID_VERSION")}")
    debugImplementation("com.squareup.leakcanary:leakcanary-support-fragment:${property("LEAK_CANARY_ANDROID_VERSION")}")
    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${property("LEAK_CANARY_ANDROID_VERSION")}")
    testImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${property("LEAK_CANARY_ANDROID_VERSION")}")

    implementation("org.greenrobot:eventbus:${property("EVENT_BUS_VERSION")}")

    implementation("com.squareup.retrofit2:retrofit:${property("RETROFIT_VERSION")}")
    implementation("com.squareup.retrofit2:converter-gson:${property("RETROFIT_VERSION")}")

    implementation("com.google.android:flexbox:${property("FLEXBOX")}")
    implementation("com.google.android.material:material:${property("MATERIAL")}")
}

fun getGitVersion(): String = Runtime.getRuntime().exec("git rev-parse --short HEAD").inputStream.use { it.bufferedReader().readText().trim() }