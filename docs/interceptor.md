# Interceptor

Translations: [简体中文](interceptor.zh.md)

Sketch uses [Interceptor] to handle the execution process of [ImageRequest]. You can
use [Interceptor] to change the input and output of any link in the execution process.

[Interceptor] provided by Sketch core module:

* [MemoryCacheInterceptor]: Provide memory caching function to avoid repeated parsing and image
  transformation and improve performance.
* [PlaceholderInterceptor]: Provide placeholder image function, display placeholder image during
  image loading process
* [ResultCacheInterceptor]: Provides result caching function to avoid repeated image transformation
  and improve performance
* [ThumbnailInterceptor]: Provides a thumbnail function. The thumbnail is loaded at the same time as
  the original image. The thumbnail is displayed first. After the original image is loaded, the
  thumbnail display is replaced to improve the user experience.
* [TransformationInterceptor]: Provides transformation function. After the image is loaded, the
  image can be transformed, such as circular, rounded, blurred, etc.
* [FetcherInterceptor]: Provides the function of loading original image data from Uri, and loading
  original image data from the network, files, resources and other sources for Decoder to decode.
* [UseSkiaInterceptor]: Convert PhotosAssetDataSource from Photos Library on iOS platform to
  ByteArrayDataSource that can be decoded by Skia
* [DecoderInterceptor]: Provides decoding function to decode the original image data into Bitmap

[Interceptor] provided by sketch-extensions-core module:

* [PauseLoadWhenScrollingInterceptor]: Provides the function of pausing the loading of pictures when
  the list slides, and resuming the loading of pictures when the slide stops.
* [SaveCellularTrafficInterceptor]: Provides the function to prohibit loading images from the
  network in non-Wi-Fi network environments

## Custom Interceptor

First implement the [Interceptor] interface to define your Interceptor, as follows:

```kotlin
class MyInterceptor : Interceptor {

    // If the current Interceptor will modify the returned results and is only used for some requests, then please give a unique key to build the cache key, otherwise give null
    override val key: String? = null

    // Used for sorting, the larger the value, the further back in the list. The value range is 0 ~ 100. Usually zero. Only DecoderInterceptor can be 100
    override val sortWeight: Int = 0

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        // Disable memory caching for all requests
        val newRequest = chain.request.newRequest {
            memoryCachePolicy(CachePolicy.DISABLED)
        }
        return chain.proceed(newRequest)
    }
}
```

> [!TIP]
> 1. MyInterceptor demonstrates a case where all requests are prohibited from using the
     memory cache
> 2. If you want to modify the return result, just intercept the result returned by the proceed
     method and return a new [ImageData]
> 3. If you don’t want to execute the request anymore, just don’t execute the proceed method.

Then register your Interceptor as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
  components {
    addInterceptor(MyInterceptor())
  }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addInterceptor(MyInterceptor())
    }
}
```

[Interceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/Interceptor.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageResult]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[ImageData]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageData.kt

[MemoryCacheInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/MemoryCacheInterceptor.kt

[PlaceholderInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/internal/PlaceholderInterceptor.kt

[ResultCacheInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/ResultCacheInterceptor.kt

[ThumbnailInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/internal/ThumbnailInterceptor.kt

[TransformationInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/transform/internal/TransformationInterceptor.kt

[FetcherInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/FetcherInterceptor.kt

[UseSkiaInterceptor]: ../sketch-core/src/iosMain/kotlin/com/github/panpf/sketch/decode/internal/UseSkiaInterceptor.kt

[DecoderInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/internal/DecoderInterceptor.kt

[PauseLoadWhenScrollingInterceptor]: ../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/request/PauseLoadWhenScrollingInterceptor.kt

[SaveCellularTrafficInterceptor]: ../sketch-extensions-core/src/commonMain/kotlin/com/github/panpf/sketch/request/SaveCellularTrafficInterceptor.kt