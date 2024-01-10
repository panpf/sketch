# Decode Interceptor

Translations: [简体中文](decode_interceptor_zh.md)

Sketch's decoding process supports interceptors, which allow you to change the input and output
before and after decoding

First, implement the [DecodeInterceptor] interface to implement your [DecodeInterceptor], as
follows:

```kotlin
class MyDecodeInterceptor : DecodeInterceptor {

    // If the current DecodeInterceptor modifies the returned result and is only used for partial requests, 
    // then give a distinct key to build the cache key, otherwise null is fine
    override val key: String = "MyDecodeInterceptor"

    // Used for sorting, the higher the value, the lower it is in the list. The value range is 0 ~ 100. 
    // Usually zero. Only EngineDecodeInterceptor can be 100
    override val sortWeight: Int = 0

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain,
    ): Result<DecodeResult> {
        val newRequest = chain.request.newRequest {
            bitmapConfig(Bitmap.Config.ARGB_4444)
        }
        return chain.proceed(newRequest)
    }
}
```

> 1. MyDecodeInterceptor demonstrates an example of changing the Bitmap.Config for all
     requests to ARGB_4444
> 2. If you want to modify the result, just intercept the result returned by the proceed method and
     return a new [DecodeResult]
> 3. If you want to stop executing requests, just don't execute the proceed method

Then, register your DecodeInterceptor via the addDecodeInterceptor() and
addDrawableDecodeInterceptor() methods as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addDecodeInterceptor(MyDecodeInterceptor())
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("file:///sdcard/sample.mp4") {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}
```

[DecodeInterceptor]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/DecodeInterceptor.kt

[DecodeResult]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/DecodeResult.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt