// After turning on TYPESAFE_PROJECT_ACCESSORS, the root directory name and sketch module name cannot be the same.
rootProject.name = "sketch-root"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")   // ktor 3.3.0-wasm2
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
include(":sketch-animated")
include(":sketch-animated-koralgif")
include(":sketch-compose")
include(":sketch-compose-core")
include(":sketch-compose-resources")
include(":sketch-core")
include(":sketch-extensions-core")
include(":sketch-extensions-compose")
include(":sketch-extensions-view")
include(":sketch-extensions-view-ability")
include(":sketch-http-core")
include(":sketch-http-ktor")
include(":sketch-http-okhttp")
include(":sketch-singleton")
include(":sketch-svg")
include(":sketch-video")
include(":sketch-video-ffmpeg")
include(":sketch-view")
include(":sketch-view-core")

/*
 * Private
 */
include(":sample")
include(":internal:images")
include(":internal:test-utils")
include(":internal:test-utils-core")
include(":internal:test-utils-compose")
include(":internal:test-utils-compose-core")