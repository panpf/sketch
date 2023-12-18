pluginManagement {
    repositories {
//        maven { setUrl("https://maven.aliyun.com/repository/public") }  // central、jcenter
//        maven { setUrl("https://maven.aliyun.com/repository/google") }  // google
//        maven { setUrl("https://repo.huaweicloud.com/repository/maven/") }    // central、google、jcenter
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
//        maven { setUrl("https://maven.aliyun.com/repository/public") }  // central、jcenter
//        maven { setUrl("https://maven.aliyun.com/repository/google") }  // google
//        maven { setUrl("https://repo.huaweicloud.com/repository/maven/") }    // central、google、jcenter
        mavenCentral()
        google()
        maven { setUrl("https://www.jitpack.io") }
//        maven { setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots") }
//        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

include(":sample") // Private
include(":sketch")
include(":sketch-compose")
include(":sketch-compose-core")
include(":sketch-core")
include(":sketch-extensions")
include(":sketch-extensions-core")
include(":sketch-extensions-compose")
include(":sketch-extensions-view")
include(":sketch-extensions-view-core")
include(":sketch-gif")
include(":sketch-gif-movie")
include(":sketch-gif-koral")
include(":sketch-okhttp")
include(":sketch-resources") // Private
include(":sketch-svg")
include(":sketch-test") // Private
include(":sketch-test-singleton") // Private
include(":sketch-video")
include(":sketch-video-ffmpeg")
include(":sketch-viewability")
include(":sketch-zoom")