# Load Icon For Apk Or Installed App

Translations: [简体中文](apk_app_icon_zh.md)

> [!IMPORTANT]
> 1. Required import `sketch-extensions-view` or `sketch-extensions-compose` module
> 2. Only supports Android platform

### Load Apk Icon

```kotlin
// Register for all ImageRequests when customizing Sketch
val sketch = Sketch.Builder(context).apply {
    components {
        addDecoder(ApkIconDecoder.Factory())
    }
}.build()
// Then just pass in the path to the apk file when loading the image.
sketch.enqueue(ImageRequest(context, uri = "/sdcard/sample.apk"))

// Or register for a single ImageRequest when loading an image
ImageRequest(context, uri = "/sdcard/sample.apk") {
    components {
        addDecoder(ApkIconDecoder.Factory())
    }
}
```

### Load the icon of the installed app

First, register [AppIconUriFetcher] as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
val sketch = Sketch.Builder(context).apply {
    components {
        addFetcher(AppIconUriFetcher.Factory())
    }
}.build()
// Then use the `newAppIconUri()` function to create a dedicated uri when loading the image.
sketch.enqueue(ImageRequest(context, uri = newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1)))

// Or register for a single ImageRequest when loading an image
ImageRequest(context, uri = newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1)) {
    components {
        addFetcher(AppIconUriFetcher.Factory())
    }
}
```

* versionCode：The versionCode of the app. The correct version number must be passed in, because
  when the icon is modified, the modified icon will be cached on the disk. If you only use
  packageName is used as the cache key, so the icon will not be refreshed even if the cache is
  changed after the App version is updated.

[comment]: <> (classs)


[ApkIconDecoder]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt