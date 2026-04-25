# Fetcher

Translations: [简体中文](fetcher.zh.md)

[Fetcher] is used to get data from Uri and return [DataSource] for [Decoder] to decode images, every
Uri supported by Sketch
There are corresponding [Fetcher] to provide support for it, as shown in the following table:

| Fetcher                     | URI                            | Create                  | Dependent modules                   | Android | iOS | Desktop | Web |
|-----------------------------|:-------------------------------|-------------------------|-------------------------------------|---------|:----|:--------|:----|
| [KtorHttpUriFetcher]        | http://, https://              | -                       | sketch-http-ktor2,sketch-http-ktor3 | ✅       | ✅   | ✅       | ✅   |
| [HurlHttpUriFetcher]        | http://, https://              | -                       | sketch-http-hurl                    | ✅       | ❌   | ✅       | ❌   |
| [OkHttpHttpUriFetcher]      | http://, https://              | -                       | sketch-http-okhttp                  | ✅       | ❌   | ✅       | ❌   |
| [FileUriFetcher]            | file://, file:/, /, D:\\, \\\\ | newFileUri()            | -                                   | ✅       | ✅   | ✅       | ✅   |
| [ComposeResourceUriFetcher] | file:///compose_resource/      | newComposeResourceUri() | sketch-compose-resources            | ✅       | ✅   | ✅       | ✅   |
| [ContentUriFetcher]         | content://                     | -                       | -                                   | ✅       | ❌   | ❌       | ❌   |
| [AssetUriFetcher]           | file:///android_asset/         | newAssetUri()           | -                                   | ✅       | ❌   | ❌       | ❌   |
| [ResourceUriFetcher]        | android.resource://            | newResourceUri()        | -                                   | ✅       | ❌   | ❌       | ❌   |
| [AppIconUriFetcher]         | app.icon://                    | newAppIconUri()         | sketch-extensions-appicon           | ✅       | ❌   | ❌       | ❌   |
| [PhotosAssetFetcher]        | file:///photos_asset/          | newPhotosAssetUri()     | -                                   | ❌       | ✅   | ❌       | ❌   |
| [KotlinResourceUriFetcher]  | file:///kotlin_resource/       | newKotlinResourceUri()  | -                                   | ❌       | ✅   | ✅       | ❌   |
| [Base64UriFetcher]          | data:image/jpeg;base64         | newBase64Uri()          | -                                   | ✅       | ✅   | ✅       | ✅   |
| [BlurHashUriFetcher]        | blurhash://                    | newBlurHashUri()        | sketch-blurhash                     | ✅       | ✅   | ✅       | ✅   |

The uses of each [Fetcher] are as follows:

* [KtorHttpUriFetcher]: Use Ktor to load images from the network. [Learn more](http.md)
* [HurlHttpUriFetcher]: Use the HttpURLConnection that comes with jvm to load images from the
  network. [Learn more](http.md)
* [OkHttpHttpUriFetcher]: Use OkHttp to load images from the network. [Learn more](http.md)
* [FileUriFetcher]: Use local file path or file uri to load images
* [ComposeResourceUriFetcher]: is used to load images from the composeResources directory of
  Compose Multiplatform, it also needs to depend on the `sketch-compose-resources` module.
* [ContentUriFetcher]: ContentResolver for Android to load images
* [AssetUriFetcher]: is used to load images from the Android assets directory
* [ResourceUriFetcher]: is used to load images from Android's resources directory
* [AppIconUriFetcher]: is used to load the icon of the installed App. It also needs to rely
  on `sketch-extensions-appicon`
  module. [Learn more](apk_app_icon.md#Load-installed-app-icon)
* [PhotosAssetFetcher]: Supports loading images from iOS Photos Library
* [KotlinResourceUriFetcher]: is used to load images from the resources directory of kotlin
* [Base64UriFetcher]: is used to load images from the base64 data block of the uri itself
* [BlurHashUriFetcher]：Supports loading images from BlurHash strings, it also needs to depend on
  `sketch-extensions-appicon` module. [Learn more](blurhash.md)

> [!IMPORTANT]
> * The built-in Fetchers that do not rely on additional modules have been registered.
> * Fetchers that rely on additional modules also support automatic registration. You only need to
    configure the dependencies.
> * If you need to register manually, please read the
    documentation: [《Register component》](register_component.md)

### Load images from the network

To load images from the network, please refer to the documentation: [《Http network image》](http.md)

### Load images from local

Loading images from local does not require additional modules, just use file path or file uri
directly, as follows:

```kotlin
val imageUri1 = "file:///storage/emulated/0/Pictures/image.jpg"
val imageUri2 = "file:/storage/emulated/0/Pictures/image.jpg"
val imageUri3 = "/storage/emulated/0/Pictures/image.jpg"
val imageUri4 = "D:\\images\\image.jpg" // Windows only
val imageUri5 = "\\qnap\\photos\\image.jpg" // Windows network path only

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// android view
imageView.loadImage(imageUri)
```

### Load images from Android assets

Images from the Android assets directory do not need to rely on additional modules. You can directly
use newAssetUri() to create the uri, as follows:

```kotlin
val imageUri = newAssetUri("image.jpg")
val imageUri2 = newAssetUri("images/image.jpg")

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// android view
imageView.loadImage(imageUri)
```

### Load images from Android ContentProvider

Loading images from Android ContentProvider does not require additional modules, just use content
uri directly, as follows:

```kotlin
val imageUri = "content://media/external/file/13841"

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// android view
imageView.loadImage(imageUri)
```

### Load images from Android res

Loading images from the Android res directory does not require additional modules. You can directly
use the newResourceUri() function to create the uri, as follows:

```kotlin
val imageUri = newResourceUr(R.drawable.ic_launcher)
val imageUri2 = newResourceUr("drawable", "ic_launcher")
// Load images from other apps
val imageUri = newResourceUr("com.android.launcher", R.drawable.ic_launcher)
// Load images from other apps
val imageUri2 = newResourceUr("com.android.launcher", "drawable", "ic_launcher")

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// android view
imageView.loadImage(imageUri)
```

### Load installed app icon

To load the installed App icon, please refer to the
documentation: [《Load installed app icon》](apk_app_icon.md#Load-installed-app-icon)

### Load images from the composeResources directory of Compose Multiplatform

To load images from the composeResources directory of Compose Multiplatform, you need to install the
`sketch-compose-resources` module first, and then use the newComposeResourceUri() function to create
the uri, as follows:

```kotlin
val imageUri =
    newComposeResourceUri("composeResources/com.github.panpf.sketch.sample.resources/files/sample.png")
val imageUri2 = newComposeResourceUri(Res.getUri("files/sample.png"))

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)
```

### Load images from kotlin's resources directory

> [!IMPORTANT]
> Only supports iOS and Desktop platforms

Loading images from the kotlin resources directory does not require additional modules. You can
directly use the newKotlinResourceUri() function to create the uri, as follows:

```kotlin
val imageUri = newKotlinResourceUri("images/sample.png")

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// android view
imageView.loadImage(imageUri)
```

### Load images from the iOS Photos Library

There is no need to rely on additional modules to load images from the iOS Photos Library. You can
directly use the newPhotosAssetUri() function to create the uri, as follows:

```kotlin
val imageUri = newPhotosAssetUri("DB16113B-984A-4D12-B4D0-50FC46066781/L0/001")

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// android view
imageView.loadImage(imageUri)
```

You can also specify whether to load thumbnails or original images through the
`preferThumbnailForPhotosAsset` and `allowNetworkAccessPhotosAsset` parameters, and whether to allow
images to be loaded from iCloud, as follows:

```kotlin
val imageUri = newPhotosAssetUri("DB16113B-984A-4D12-B4D0-50FC46066781/L0/001")
val request = ImageRequest(context, imageUri) {
    // Thumbnails are loaded first. The default value is false, which means the original image is loaded first.
    preferThumbnailForPhotosAsset(true)
    // Allow loading images from iCloud. The default value is false, which means loading images from iCloud is not allowed.
    allowNetworkAccessPhotosAsset(true)
}
AsyncImage(
    request = request,
    contentDescription = "photo"
)
```

### Load images from BlurHash strings

Please refer to the documentation for loading images from BlurHash
strings: [《BlurHash》](blurhash.md)

### Load image from base64 string

Loading images from Base64 strings does not require additional modules. You can directly use the
newBase64Uri() function to create the uri, as follows:

```kotlin
val base64String = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxISEhUTE"
val imageUri = "data:image/jpeg;base64,${base64String}"
val imageUri = newBase64Uri("image/jpeg", base64String)

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// android view
imageView.loadImage(imageUri)
```

### Custom Fetcher

First implement the [Fetcher] interface to define your Fetcher and its Factory

Pay attention to the value of the [Fetcher].sortWeight attribute, which determines the position of
the current [Fetcher] in the Fetcher list. The larger the value, the further back in the list. The
value range is 0 ~ 100

Then refer to the document [《Register component》](register_component.md) to register your Fetcher

[comment]: <> (classs)

[Sketch]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Decoder]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Fetcher]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Fetcher.kt

[FetchResult]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FetchResult.kt

[AssetUriFetcher]: ../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/AssetUriFetcher.kt

[Base64UriFetcher]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Base64UriFetcher.kt

[ContentUriFetcher]: ../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/ContentUriFetcher.kt

[FileUriFetcher]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FileUriFetcher.kt

[HurlHttpUriFetcher]: ../sketch-http-hurl/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HurlHttpUriFetcher.kt

[OkHttpHttpUriFetcher]: ../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/fetch/OkHttpHttpUriFetcher.kt

[KtorHttpUriFetcher]: ../sketch-http-ktor3-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/KtorHttpUriFetcher.kt

[ResourceUriFetcher]: ../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/ResourceUriFetcher.kt

[AppIconUriFetcher]: ../sketch-extensions-appicon/src/main/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[KotlinResourceUriFetcher]: ../sketch-core/src/desktopMain/kotlin/com/github/panpf/sketch/fetch/KotlinResourceUriFetcher.kt

[ComposeResourceUriFetcher]: ../sketch-compose-resources/src/commonMain/kotlin/com/github/panpf/sketch/fetch/ComposeResourceUriFetcher.kt

[BlurHashUriFetcher]: ../sketch-blurhash/src/commonMain/kotlin/com/github/panpf/sketch/fetch/BlurHashUriFetcher.kt

[PhotosAssetFetcher]: ../sketch-core/src/iosMain/kotlin/com/github/panpf/sketch/fetch/PhotosAssetFetcher.kt

[DataSource]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/source/DataSource.kt