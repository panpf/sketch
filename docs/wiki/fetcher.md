# Fetcher

Translations: [简体中文](fetcher_zh.md)

[Fetcher] is used to get data from uri, return [FetchResult], and hand it over to [Decoder] for use.

Sketch has a corresponding [Fetcher] implementation for each uri supported, and there are the
following types:

* [AssetUriFetcher][AssetUriFetcher]: Load images from the app’s assets directory
* [Base64UriFetcher][Base64UriFetcher]: Load an image in base 64 format from the uri itself
* [ContentUriFetcher][ContentUriFetcher]: Load images from ContentResolver
* [FileUriFetcher][FileUriFetcher]: Load images from local files
* [HttpUriFetcher][HttpUriFetcher]: Load image from http uri
* [ResourceUriFetcher][ResourceUriFetcher]: Load images from Android Resource
* [AppIconUriFetcher][AppIconUriFetcher]: Load the icon from the installed
  app, [Learn more](apk_app_icon.md#displays-an-icon-for-the-installed-app)

### Extend Fetcher

First you need to implement the [Fetcher] interface to define your [Fetcher] and its Factory, as
follows:

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
```

Then register through the addFetcher method, as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(context).apply {
            components {
                addFetcher(MyFetcher.Factory())
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage(context, "myUri://sample.jpeg") {
    components {
        addFetcher(MyFetcher.Factory())
    }
}
```

[comment]: <> (classs)

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Fetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Fetcher.kt

[FetchResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FetchResult.kt

[AssetUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/AssetUriFetcher.kt

[Base64UriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Base64UriFetcher.kt

[ContentUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/ContentUriFetcher.kt

[FileUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FileUriFetcher.kt

[HttpUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[ResourceUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/ResourceUriFetcher.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt