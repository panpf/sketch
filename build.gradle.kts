buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.android.plugin)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.kotlin.serialization.plugin)
        classpath(libs.androidx.navigation.safeargsgradle.plugin)
        classpath(libs.maven.publish.plugin)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven/") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
