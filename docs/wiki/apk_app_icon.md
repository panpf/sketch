# Display An Icon For An Apk File Or Installed App

Translations: [简体中文](apk_app_icon_zh.md)

> [!IMPORTANT]
> Required import `sketch-extensions-view` or `sketch-extensions-compose` module

### Displays an icon for the APK file

First, register [ApkIconDecoder] as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                supportApkIcon()
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("/sdcard/sample.apk") {
    components {
        supportApkIcon()
    }
}
```

Then, pass in the path of the apk file when the image is displayed, as follows:

```kotlin
imageView.displayImage("/sdcard/sample.apk")
```

### Displays an icon for the installed app

First, register [AppIconUriFetcher] as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                supportAppIcon()
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage(newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1)) {
    components {
        supportAppIcon()
    }
}
```

Then use the 'newAppIconUri()' function to create a private uri and execute the display as follows:

```kotlin
imageView.displayImage(newAppIconUri("com.github.panpf.sketch.sample", versionCode = 1))
```

* versionCode：The version code of the app, which must be passed in the correct version number.
  Because when you make a modification to the icon, the modified icon will be cached on the disk, if
  you only use it packageName is used as the cache key, so the icon will not be refreshed even if
  the app version is updated, even if it changes

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.kt

[ApkIconDecoder]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt