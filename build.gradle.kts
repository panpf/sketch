buildscript {
    repositories {
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven/") }
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${property("ANDROID_BUILD")}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("KOTLIN")}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${property("ANDROIDX_NAVIGATION")}")
        classpath("io.github.panpf.maven-publish:maven-publish-gradle-plugin:${property("MAVEN_PUBLISH")}")
    }
}

allprojects {
    repositories {
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven/") }
        mavenCentral()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
