# Fetcher

Translations: [简体中文](fetcher_zh.md)

[Fetcher] is used to get data from uri, return [FetchResult], and hand it over to [Decoder] for use.

Each uri supported by [Sketch] has a corresponding [Fetcher] implementation, as shown in the
following table:

| URI                    | Fetcher                     | Create                  | Dependent modules        | Android | iOS | Desktop | Web |
|:-----------------------|-----------------------------|-------------------------|--------------------------|---------|:----|:--------|:----|
| http://, https://      | [HttpUriFetcher]            | -                       | -                        | ✅       | ✅   | ✅       | ✅   |
| file://, /             | [FileUriFetcher]            | newFileUri()            | -                        | ✅       | ✅   | ✅       | ✅   |
| compose.resource://    | [ComposeResourceUriFetcher] | newComposeResourceUri() | sketch-compose-resources | ✅       | ✅   | ✅       | ✅   |
| data:image/, data:img/ | [Base64UriFetcher]          | newBase64Uri()          | -                        | ✅       | ✅   | ✅       | ✅   |
| asset://               | [AssetUriFetcher]           | newAssetUri()           | -                        | ✅       | ❌   | ❌       | ❌   |
| content://             | [ContentUriFetcher]         | -                       | -                        | ✅       | ❌   | ❌       | ❌   |
| android.resource://    | [ResourceUriFetcher]        | newResourceUri()        | -                        | ✅       | ❌   | ❌       | ❌   |
| app.icon://            | [AppIconUriFetcher]         | newAppIconUri()         | sketch-extensions-core   | ✅       | ❌   | ❌       | ❌   |
| kotlin.resource://     | [KotlinResourceUriFetcher]  | newKotlinResourceUri()  | -                        | ❌       | ✅   | ✅       | ❌   |

* [AssetUriFetcher] is used to load images from the Android assets directory
* [ContentUriFetcher] ContentResolver for Android to load images
* [ResourceUriFetcher] is used to load images from Android's resources directory
* [AppIconUriFetcher] is used to load the icon of the installed App. It also needs to rely
    on `sketch-extensions-core`
    module. [Learn more](apk_app_icon.md#load-the-icon-of-the-installed-app)
* [Base64UriFetcher] is used to load images from the base64 data block of the uri itself
* [ComposeResourceUriFetcher] is used to load images from the composeResources directory of
    Compose Multiplatform, it also needs to depend on the `sketch-compose-resources` module.
* [KotlinResourceUriFetcher] is used to load images from the resources directory of kotlin

## Register Fetcher

[Fetcher] that needs to rely on a separate module (such as [ComposeResourceUriFetcher]) needs to be
registered when initializing Sketch, as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        addFetcher(ComposeResourceUriFetcher.Factory())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "https://www.example.com/image.gif") {
    components {
        addFetcher(ComposeResourceUriFetcher.Factory())
    }
}
```

### Extend Fetcher

First implement the [Fetcher] interface to define your Fetcher and its Factory, and then register it
through the addFetcher() method, as follows:

```kotlin
class MyFetcher : Fetcher {

    override suspend fun fetch(): Result<FetchResult> {
        // Parse your uri here and get the data
    }

    companion object {
        const val MY_SCHEME = "myUri"
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): MyFetcher? {
            return if (request.uri.startWith("$MY_SCHEME://")) {
                MyFetcher()
            } else {
                null
            }
        }
    }
}

// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        addFetcher(MyFetcher.Factory())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "myUri://sample.jpeg") {
    components {
        addFetcher(MyFetcher.Factory())
    }
}
```

[comment]: <> (classs)

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Fetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Fetcher.kt

[FetchResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FetchResult.kt

[AssetUriFetcher]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/AssetUriFetcher.kt

[Base64UriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Base64UriFetcher.kt

[ContentUriFetcher]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/ContentUriFetcher.kt

[FileUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FileUriFetcher.kt

[HttpUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[ResourceUriFetcher]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/ResourceUriFetcher.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[KotlinResourceUriFetcher]: ../../sketch-core/src/desktopMain/kotlin/com/github/panpf/sketch/fetch/KotlinResourceUriFetcher.kt

[ComposeResourceUriFetcher]: ../../sketch-compose-resources/src/commonMain/kotlin/com/github/panpf/sketch/fetch/ComposeResourceUriFetcher.kt