// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        jcenter()
        google()
        maven { setUrl("https://dl.google.com/dl/android/maven2/") }
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven/") } // Huawei Maven mirrors
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${property("ANDROID_BUILD_VERSION")}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("KOTLIN_VERSION")}")
//        classpath("guru.stefma.bintrayrelease:bintrayrelease:${property("BINTRAY_RELEASE_VERSION")}")
        classpath("com.novoda:bintray-release:0.9")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        maven { setUrl("https://dl.google.com/dl/android/maven2/") }
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven/") } // Huawei Maven mirrors
        maven { setUrl("https://dl.bintray.com/panpf/maven") }
    }
}
