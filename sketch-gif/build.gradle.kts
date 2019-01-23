import com.android.build.gradle.BaseExtension
import com.novoda.gradle.release.PublishExtension

plugins {
    id("com.android.library")
//    id("guru.stefma.bintrayrelease")
    id("com.novoda.bintray-release")
}

configure<BaseExtension> {
    compileSdkVersion(property("COMPILE_SDK_VERSION").toString().toInt())

    defaultConfig {
        minSdkVersion(property("MIN_SDK_VERSION").toString().toInt())
        targetSdkVersion(property("TARGET_SDK_VERSION").toString().toInt())
        versionCode = property("VERSION_CODE").toString().toInt()
        versionName = property("VERSION_NAME").toString()

        consumerProguardFiles("proguard-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.annotation:annotation:${property("ANDROIDX_ANNOTATION")}")
}

//apply { from("build_upload.gradle.kts") }

//Properties().apply { project.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) } }.takeIf { !it.isEmpty }?.let { localProperties ->
//    //    apply { plugin("guru.stefma.bintrayrelease") }
//
////    version = android.defaultConfig.versionName
//    version = property("VERSION_NAME").toString()
//    group = "me.panpf"
//    configure<PublishExtension> {
//        artifactId = "sketch-gif"
//        desc = "Android, Image, Load, GIF"
//        website = "https://github.com/panpf/sketch"
//        userOrg = localProperties.getProperty("bintray.userOrg")
//        bintrayUser = localProperties.getProperty("bintray.user")
//        bintrayKey = localProperties.getProperty("bintray.apikey")
//    }
//}

Properties().apply { project.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) } }.takeIf { !it.isEmpty }?.let { localProperties ->
    configure<PublishExtension> {
        groupId = "me.panpf"
        artifactId = "sketch-gif"
        publishVersion = property("VERSION_NAME").toString()
        desc = "Android, Image, Load, GIF"
        website = "https://github.com/panpf/sketch"
        userOrg = localProperties.getProperty("bintray.userOrg")
        bintrayUser = localProperties.getProperty("bintray.user")
        bintrayKey = localProperties.getProperty("bintray.apikey")
    }
}