# Load Icon For Apk Or Installed App

Translations: [简体中文](apk_app_icon_zh.md)

> [!IMPORTANT]
> 1. Required import `sketch-extensions-view` or `sketch-extensions-compose` module
> 2. Only supports Android platform

### Load Apk Icon

First, register [ApkIconDecoder] as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        supportApkIcon()
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "/sdcard/sample.apk") {
    components {
        supportApkIcon()
    }
}
```

Then, just pass in the path to the apk file when loading the image, as follows:

```kotlin
imageView.displayImage("/sdcard/sample.apk")
```

### Load the icon of the installed app

First, register [AppIconUriFetcher] as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        supportAppIcon()
    }
}.build()

// Register for a single ImageRequest when loading an image
imageView.displayImage(newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1)) {
    components {
        supportAppIcon()
    }
}
```

Then, use the `newAppIconUri()` function to create a dedicated uri when loading images, as follows:

```kotlin
imageView.displayImage(newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1))
```

* versionCode：The versionCode of the app. The correct version number must be passed in, because
  when the icon is modified, the modified icon will be cached on the disk. If you only use
  packageName is used as the cache key, so the icon will not be refreshed even if the cache is
  changed after the App version is updated.

[comment]: <> (classs)


[ApkIconDecoder]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt