# DecodeInterceptor

Translations: [简体中文](decode_interceptor_zh.md)

The decoding process of Sketch supports changing the input and output before and after decoding
through interceptors.

First implement the [DecodeInterceptor] interface to implement your DecodeInterceptor, and then
register it through the addDecodeInterceptor() method, as follows:

```kotlin
class MyDecodeInterceptor : DecodeInterceptor {

    // If the current DecodeInterceptor will modify the returned results and is only used for some requests, then please give a unique key to build the cache key, otherwise give null
    override val key: String = "MyDecodeInterceptor"

    // Used for sorting, the larger the value, the further back in the list. The value range is 0 ~ 100. Usually zero. Only EngineDecodeInterceptor can be 100
    override val sortWeight: Int = 0

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain,
    ): Result<DecodeResult> {
        val newRequest = chain.request.newRequest {
            colorType(Bitmap.Config.RGB_565)
        }
        return chain.proceed(newRequest)
    }
}

// Register for all ImageRequests when customizing Sketch
Sketch.Builder(context).apply {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}.build()

// Register for a single ImageRequest when loading an image
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}
```

> [!TIP]
> 1. MyDecodeInterceptor demonstrates a case of changing the Bitmap.Config of all requests to
     RGB_565
> 2. If you want to modify the return result, just intercept the result returned by the proceed
     method and return a new [DecodeResult]
> 3. If you don’t want to execute the request anymore, just don’t execute the proceed method.

[comment]: <> (classs)

[DecodeInterceptor]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/DecodeInterceptor.kt

[DecodeResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/DecodeResult.kt