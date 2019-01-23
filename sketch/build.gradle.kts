import com.android.build.gradle.BaseExtension

plugins {
    id("com.android.library")
}
//apply(plugin = "com.android.library")
//apply(from = "build_test.gradle")

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
    compileOnly(project(":sketch-gif"))
    implementation("androidx.annotation:annotation:${property("ANDROIDX_ANNOTATION")}")
}

//apply(from = "build_upload.gradle")
