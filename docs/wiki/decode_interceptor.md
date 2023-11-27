# Decode Interceptor

Translations: [简体中文](decode_interceptor_zh.md)

Sketch's decoding process supports interceptors, which allow you to change the input and output
before and after decoding

Sketch divides decoding into two types: Drawable and Bitmap, so interception is also divided into
two types [BitmapDecodeInterceptor]
and [DrawableDecodeInterceptor]

First, implement the [BitmapDecodeInterceptor] or [DrawableDecodeInterceptor] interface to define
your DecodeInterceptor, as follows:

```kotlin
class MyBitmapDecodeInterceptor : BitmapDecodeInterceptor {

    // If the current BitmapDecodeInterceptor modifies the returned result and is only used for partial requests, 
    // then give a distinct key to build the cache key, otherwise null is fine
    override val key: String = "MyBitmapDecodeInterceptor"

    // Used for sorting, the higher the value, the lower it is in the list. The value range is 0 ~ 100. 
    // Usually zero. Only EngineBitmapDecodeInterceptor can be 100
    override val sortWeight: Int = 0

    @WorkerThread
    override suspend fun intercept(
        chain: BitmapDecodeInterceptor.Chain,
    ): Result<BitmapDecodeResult> {
        val newRequest = chain.request.newRequest {
            bitmapConfig(Bitmap.Config.ARGB_4444)
        }
        return chain.proceed(newRequest)
    }
}

class MyDrawableDecodeInterceptor : DrawableDecodeInterceptor {

    // If the current DrawableDecodeInterceptor modifies the returned result and is only used for part of the request, 
    // then give a unique key to build the cache key, otherwise null is fine
    override val key: String = "MyDrawableDecodeInterceptor"

    // Used for sorting, the higher the value, the lower it is in the list. The value range is 0 ~ 100. 
    // Usually zero. Only EngineDrawableDecodeInterceptor can be 100
    override val sortWeight: Int = 0

    @WorkerThread
    override suspend fun intercept(
        chain: DrawableDecodeInterceptor.Chain,
    ): Result<DrawableDecodeResult> {
        val newRequest = chain.request.newRequest {
            disallowAnimatedImage()
        }
        return chain.proceed(newRequest)
    }
}
```

> 1. MyBitmapDecodeInterceptor demonstrates an example of changing the Bitmap.Config for all
     requests to ARGB_4444
> 2. MyDrawableDecodeInterceptor demonstrates a case that prohibits all requests to decode GIFs
> 3. If you want to modify the result, just intercept the result returned by the proceed method and
     return a new [BitmapDecodeResult] or [DrawableDecodeResult].
> 4. If you want to stop executing requests, just don't execute the proceed method

Then, register your DecodeInterceptor via the addBitmapDecodeInterceptor() and
addDrawableDecodeInterceptor() methods as follows:

```kotlin
/* Register for all ImageRequests */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addBitmapDecodeInterceptor(MyBitmapDecodeInterceptor())
                addDrawableDecodeInterceptor(MyDrawableDecodeInterceptor())
            }
        }.build()
    }
}

/* Register for a single ImageRequest */
imageView.displayImage("file:///sdcard/sample.mp4") {
    components {
        addBitmapDecodeInterceptor(MyBitmapDecodeInterceptor())
        addDrawableDecodeInterceptor(MyDrawableDecodeInterceptor())
    }
}
```

[BitmapDecodeInterceptor]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/BitmapDecodeInterceptor.kt

[DrawableDecodeInterceptor]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/DrawableDecodeInterceptor.kt

[BitmapDecodeResult]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/BitmapDecodeResult.kt

[DrawableDecodeResult]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/DrawableDecodeResult.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt