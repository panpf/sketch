buildscript {
    repositories {
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven/") }
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.android.buildtools)
        classpath(libs.kotlin.gradlepluginx)
        classpath(libs.kotlin.serializationpluginx)
        classpath(libs.androidx.navigation.safeargsgradlepluginx)
        classpath(libs.maven.publishpluginx)
    }
}

allprojects {
    repositories {
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven/") }
        mavenCentral()
        google()
        mavenLocal()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
