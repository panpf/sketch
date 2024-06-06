# RequestInterceptor

Translations: [简体中文](request_interceptor_zh.md)

Sketch intercepts the execution process of [ImageRequest] through [RequestInterceptor], and you can
use this to change the input and output of the execution process.

First implement the [RequestInterceptor] interface to define your RequestInterceptor, as follows:

```kotlin
class MyRequestInterceptor : RequestInterceptor {

    // If the current RequestInterceptor will modify the returned results and is only used for some requests, then please give a unique key to build the cache key, otherwise give null
    override val key: String? = null

    // Used for sorting, the larger the value, the further back in the list. The value range is 0 ~ 100. Usually zero. Only EngineRequestInterceptor can be 100
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
> 1. MyRequestInterceptor demonstrates a case where all requests are prohibited from using the
     memory cache
> 2. If you want to modify the return result, just intercept the result returned by the proceed
     method and return a new [ImageData]
> 3. If you don’t want to execute the request anymore, just don’t execute the proceed method.

Then register your RequestInterceptor as follows:

```kotlin
// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
  components {
      addRequestInterceptor(MyRequestInterceptor())
  }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addRequestInterceptor(MyRequestInterceptor())
    }
}
```

[RequestInterceptor]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/RequestInterceptor.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageResult.kt

[ImageData]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageData.kt