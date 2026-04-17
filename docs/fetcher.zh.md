# Fetcher

翻译：[English](fetcher.md)

[Fetcher] 用于从 Uri 获取数据返回 [DataSource] 供 [Decoder] 解码图像使用，Sketch 支持的每一种 Uri
都有对应的 [Fetcher] 为其提供支持，如下表所示：

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

每种 [Fetcher] 的用途如下：

* [KtorHttpUriFetcher]：使用 Ktor 从网络加载图片。[了解更多](http.zh.md)
* [HurlHttpUriFetcher]：使用 jvm 自带的 HttpURLConnection 从网络加载图片。[了解更多](http.zh.md)
* [OkHttpHttpUriFetcher]：使用 OkHttp 从网络加载图片。[了解更多](http.zh.md)
* [FileUriFetcher]：使用本地文件路径或文件 uri 加载图片
* [ComposeResourceUriFetcher]：用于从 Compose Multiplatform 的 composeResources
  目录加载图片，它还需要依赖 `sketch-compose-resources` 模块。
* [ContentUriFetcher]：用于 Android 的 ContentResolver 加载图片
* [AssetUriFetcher]：用于从 Android 的 assets 目录加载图片
* [ResourceUriFetcher]：用于从 Android 的 resources 目录加载图片
* [AppIconUriFetcher]：用于加载已安装 App 的图标，它还需要依赖 `sketch-extensions-appicon`
  模块。[了解更多](apk_app_icon.zh.md#加载已安装-App-的图标)
* [PhotosAssetFetcher]：支持从 iOS 的 Photos Library 加载图片
* [KotlinResourceUriFetcher]：用于从 kotlin 的 resources 目录加载图片
* [Base64UriFetcher]：用于从 uri 本身的 base64 数据块中加载图片
* [BlurHashUriFetcher]：支持从 BlurHash 字符串加载图片，它还需要依赖 `sketch-extensions-appicon`
  模块。[了解更多](blurhash.zh.md)

> [!IMPORTANT]
> * 内置的不依赖额外模块的 Fetcher 都已经注册了
> * 依赖额外模块的 Fetcher 也都支持自动注册，你只需要配置好依赖即可
> * 如果你需要手动注册，请阅读文档：[《注册组件》](register_component.zh.md)

### 从网络加载图片

从网络加载图片请参考文档：[《Http 网络图片》](http.zh.md)

### 从本地加载图片

从本地加载图片不需要依赖额外的模块，直接使用 file path 或者 file uri 即可，如下：

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

### 从 Android assets 加载图片

从 Android assets 目录图片不需要依赖额外的模块，直接使用 newAssetUri() 创建 uri 即可，如下：

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

### 从 Android ContentProvider 加载图片

从 Android ContentProvider 加载图片不需要依赖额外的模块，直接使用 content uri 即可，如下：

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

### 从 Android res 加载图片

从 Android res 目录加载图片不需要依赖额外的模块，直接使用 newResourceUri() 函数创建 uri 即可，如下：

```kotlin
val imageUri = newResourceUr(R.drawable.ic_launcher)
val imageUri2 = newResourceUr("drawable", "ic_launcher")
// 从其它 App 中加载图片
val imageUri = newResourceUr("com.android.launcher", R.drawable.ic_launcher)    
// 从其它 App 中加载图片
val imageUri2 = newResourceUr("com.android.launcher", "drawable", "ic_launcher")    

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// android view
imageView.loadImage(imageUri)
```

### 加载已安装 App 图标

从网络加载图片请参考文档：[《加载已安装 App 的图标》](apk_app_icon.zh.md#加载已安装-app-的图标)

### 从 Compose Multiplatform 的 composeResources 目录加载图片

从 Compose Multiplatform 的 composeResources 目录加载图片需要先安装 `sketch-compose-resources`
模块，然后使用 newComposeResourceUri() 函数创建 uri 即可，如下：

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

### 从 kotlin 的 resources 目录加载图片

> [!IMPORTANT]
> 仅支持 Ios 和 Desktop 平台

从 kotlin resources 目录加载图片不需要依赖额外的模块，直接使用 newKotlinResourceUri() 函数创建 uri
即可，如下：

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

### 从 iOS 的 Photos Library 加载图片

从 iOS 的 Photos Library 加载图片不需要依赖额外的模块，直接使用 newPhotosAssetUri() 函数创建 uri
即可，如下：

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

你还可以通过 `preferThumbnailForPhotosAsset` 和 `allowNetworkAccessPhotosAsset`
参数来指定加载缩略图还是原图，以及是否允许从 iCloud 加载图片，如下：

```kotlin
val imageUri = newPhotosAssetUri("DB16113B-984A-4D12-B4D0-50FC46066781/L0/001")
val request = ImageRequest(context, imageUri) {
    // 优先加载缩略图，默认值为 false，即优先加载原图
    preferThumbnailForPhotosAsset(true)   
    // 允许从 iCloud 加载图片，默认值为 false，即不允许从 iCloud 加载图片
    allowNetworkAccessPhotosAsset(true)   
}
AsyncImage(
    request = request,
    contentDescription = "photo"
)
```

### 从 BlurHash 字符串中加载图片

从 BlurHash 字符串中加载图片请参考文档：[《BlurHash》](blurhash.zh.md)

### 从 base64 字符串中加载图片

从 Base64 字符串中加载图片不需要依赖额外的模块，直接使用 newBase64Uri() 函数创建 uri 即可，如下：

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

### 扩展 Fetcher

先实现 [Fetcher] 接口定义你的 Fetcher 和它的 Factory

然后参考文档 [《注册组件》](register_component.zh.md) 注册你的 Fetcher 即可

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