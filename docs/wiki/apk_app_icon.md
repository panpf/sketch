# Load Icon For Apk Or Installed App

Translations: [简体中文](apk_app_icon_zh.md)

## Load Apk Icon

First install the dependencies

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-extensions-apkicon:${LAST_VERSION}")
```

Then directly pass in the path to the apk file when loading the image:

```kotlin
sketch.enqueue(ImageRequest(context, uri = "/sdcard/sample.apk"))
```

> [!IMPORTANT]
> 1. `sketch-extensions-apkicon`
     The module supports automatic registration of components. For details on component
     registration, please see the documentation: [《Register component》](register_component.md)
> 2. Only supports Android platform

## Load the icon of the installed app

First install the dependencies

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-extensions-appicon:${LAST_VERSION}")
```

Then use the `newAppIconUri()` function to create a dedicated uri when loading the image.

```kotlin
// app.icon://com.github.panpf.sketch.sample/1
val appIconUri = newAppIconUri(packageName = "com.github.panpf.sketch.sample", versionCode = 1)
sketch.enqueue(ImageRequest(context, uri = appIconUri))
```

> [!IMPORTANT]
> 1. versionCode: App version number. The correct version number must be passed in, because when the
     icon is modified, the modified icon will be cached on the disk. If you only use
     packageName is used as the cache key, so the icon will not be refreshed even if the cache is
     changed after the App version is updated.
> 2. `sketch-extensions-appicon` The module supports automatic component registration. Please see
     the documentation for details on component
     registration. [《Register component》](register_component.md)
> 3. Only supports Android platform

[comment]: <> (classs)

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[ApkIconDecoder]: ../../sketch-extensions-apkicon/src/main/kotlin/com/github/panpf/sketch/decode/ApkIconDecoder.kt

[AppIconUriFetcher]: ../../sketch-extensions-appicon/src/main/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt