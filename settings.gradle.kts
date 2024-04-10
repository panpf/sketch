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
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")   // ktor wasm
//        maven { setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots") }
//        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

/*
 * Release
 */
include(":sketch")
include(":sketch-compose")
include(":sketch-compose-core")
include(":sketch-core")
include(":sketch-extensions")
include(":sketch-extensions-core")
include(":sketch-extensions-compose")
include(":sketch-extensions-view")
include(":sketch-extensions-view-core")
include(":sketch-animated")
include(":sketch-animated-koralgif")
include(":sketch-okhttp")
include(":sketch-svg")
include(":sketch-video")
include(":sketch-video-ffmpeg")
include(":sketch-viewability")

/*
 * Private
 */
include(":sample")
include(":internal:images")
include(":internal:test-utils")
include(":internal:test-utils-core")