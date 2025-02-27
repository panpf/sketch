# Fetcher

Translations: [简体中文](fetcher.zh.md)

[Fetcher] is used to get data from uri, return [FetchResult], and hand it over to [Decoder] for use.

Each uri supported by [Sketch] has a corresponding [Fetcher] implementation, as shown in the
following table:

| URI                       | Fetcher                     | Create                  | Dependent modules         | Android | iOS | Desktop | Web |
|:--------------------------|-----------------------------|-------------------------|---------------------------|---------|:----|:--------|:----|
| http://, https://         | [HurlHttpUriFetcher]        | -                       | sketch-http-hurl          | ✅       | ❌   | ✅       | ❌   |
| http://, https://         | [OkHttpHttpUriFetcher]      | -                       | sketch-http-okhttp        | ✅       | ❌   | ✅       | ❌   |
| http://, https://         | [KtorHttpUriFetcher]        | -                       | sketch-http-ktor3         | ✅       | ✅   | ✅       | ✅   |
| file://, /                | [FileUriFetcher]            | newFileUri()            | -                         | ✅       | ✅   | ✅       | ✅   |
| file:///compose_resource/ | [ComposeResourceUriFetcher] | newComposeResourceUri() | sketch-compose-resources  | ✅       | ✅   | ✅       | ✅   |
| data:image/, data:img/    | [Base64UriFetcher]          | newBase64Uri()          | -                         | ✅       | ✅   | ✅       | ✅   |
| file:///android_asset/    | [AssetUriFetcher]           | newAssetUri()           | -                         | ✅       | ❌   | ❌       | ❌   |
| content://                | [ContentUriFetcher]         | -                       | -                         | ✅       | ❌   | ❌       | ❌   |
| android.resource://       | [ResourceUriFetcher]        | newResourceUri()        | -                         | ✅       | ❌   | ❌       | ❌   |
| app.icon://               | [AppIconUriFetcher]         | newAppIconUri()         | sketch-extensions-appicon | ✅       | ❌   | ❌       | ❌   |
| file:///kotlin_resource/  | [KotlinResourceUriFetcher]  | newKotlinResourceUri()  | -                         | ❌       | ✅   | ✅       | ❌   |

* [HurlHttpUriFetcher]: Use the HttpURLConnection that comes with jvm to load images from the
  network. [Learn more](http.md)
* [OkHttpHttpUriFetcher]: Use OkHttp to load images from the network. [Learn more](http.md)
* [KtorHttpUriFetcher]: Use Ktor to load images from the network. [Learn more](http.md)
* [AssetUriFetcher]: is used to load images from the Android assets directory
* [ContentUriFetcher]: ContentResolver for Android to load images
* [ResourceUriFetcher]: is used to load images from Android's resources directory
* [AppIconUriFetcher]: is used to load the icon of the installed App. It also needs to rely
  on `sketch-extensions-core`
  module. [Learn more](apk_app_icon.md#load-the-icon-of-the-installed-app)
* [Base64UriFetcher]: is used to load images from the base64 data block of the uri itself
* [ComposeResourceUriFetcher]: is used to load images from the composeResources directory of
  Compose Multiplatform, it also needs to depend on the `sketch-compose-resources` module.
* [KotlinResourceUriFetcher]: is used to load images from the resources directory of kotlin

> [!IMPORTANT]
> The above components all support automatic registration. You only need to import them without
> additional configuration. If you need to register manually, please read the
> documentation: [《Register component》](register_component.md)

### Extend Fetcher

First implement the [Fetcher] interface to define your Fetcher and its Factory

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