# Fetcher

翻译：[English](fetcher.md)

[Fetcher] 用于从 uri 获取数据，返回 [FetchResult]，交由 [Decoder] 使用。

[Sketch] 支持的每一种 uri 都有对应的 [Fetcher] 实现，如下表所示：

| URI                       | Fetcher                     | Create                  | Dependent modules         | Android | iOS | Desktop | Web |
|:--------------------------|-----------------------------|-------------------------|---------------------------|---------|:----|:--------|:----|
| http://, https://         | [HurlHttpUriFetcher]        | -                       | sketch-http-hurl          | ✅       | ❌   | ✅       | ❌   |
| http://, https://         | [OkHttpHttpUriFetcher]      | -                       | sketch-http-okhttp        | ✅       | ❌   | ✅       | ❌   |
| http://, https://         | [KtorHttpUriFetcher]        | -                       | sketch-http-ktor3         | ✅       | ✅   | ✅       | ✅   |
| file://, /, D:/           | [FileUriFetcher]            | newFileUri()            | -                         | ✅       | ✅   | ✅       | ✅   |
| file:///compose_resource/ | [ComposeResourceUriFetcher] | newComposeResourceUri() | sketch-compose-resources  | ✅       | ✅   | ✅       | ✅   |
| data:image/jpeg;base64    | [Base64UriFetcher]          | newBase64Uri()          | -                         | ✅       | ✅   | ✅       | ✅   |
| file:///android_asset/    | [AssetUriFetcher]           | newAssetUri()           | -                         | ✅       | ❌   | ❌       | ❌   |
| content://                | [ContentUriFetcher]         | -                       | -                         | ✅       | ❌   | ❌       | ❌   |
| android.resource://       | [ResourceUriFetcher]        | newResourceUri()        | -                         | ✅       | ❌   | ❌       | ❌   |
| app.icon://               | [AppIconUriFetcher]         | newAppIconUri()         | sketch-extensions-appicon | ✅       | ❌   | ❌       | ❌   |
| file:///kotlin_resource/  | [KotlinResourceUriFetcher]  | newKotlinResourceUri()  | -                         | ❌       | ✅   | ✅       | ❌   |

* [HurlHttpUriFetcher]：使用 jvm 自带的 HttpURLConnection 从网络加载图片。[了解更多](http.zh.md)
* [OkHttpHttpUriFetcher]：使用 OkHttp 从网络加载图片。[了解更多](http.zh.md)
* [KtorHttpUriFetcher]：使用 Ktor 从网络加载图片。[了解更多](http.zh.md)
* [AssetUriFetcher]：用于从 Android 的 assets 目录加载图片
* [ContentUriFetcher]：用于 Android 的 ContentResolver 加载图片
* [ResourceUriFetcher]：用于从 Android 的 resources 目录加载图片
* [AppIconUriFetcher]：用于加载已安装 App 的图标，它还需要依赖 `sketch-extensions-core`
  模块。[了解更多](apk_app_icon.zh.md#加载已安装-App-的图标)
* [Base64UriFetcher]：用于从 uri 本身的 base64 数据块中加载图片
* [ComposeResourceUriFetcher]：用于从 Compose Multiplatform 的 composeResources
  目录加载图片，它还需要依赖 `sketch-compose-resources` 模块。
* [KotlinResourceUriFetcher]：用于从 kotlin 的 resources 目录加载图片

> [!IMPORTANT]
> 上述组件都支持自动注册，你只需要导入即可，无需额外配置，如果你需要手动注册，
> 请阅读文档：[《注册组件》](register_component.zh.md)

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