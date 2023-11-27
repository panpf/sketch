# Decode Interceptor

Sketch 的解码过程支持拦截器，你可以通过拦截器来拦截解码过程改变解码前后的输入和输出

Sketch 将解码分为 Drawable 和 Bitmap 两种，因此拦截也同样分为两种 [BitmapDecodeInterceptor]
和 [DrawableDecodeInterceptor]，如下：

```kotlin
class MyBitmapDecodeInterceptor : BitmapDecodeInterceptor {

    // 如果当前 BitmapDecodeInterceptor 会修改返回的结果并且仅用于部分请求，那么请给一个不重复的 key 用于构建缓存 key，否则给 null 即可
    override val key: String = "MyBitmapDecodeInterceptor"

    // 用于排序，值越大在列表中越靠后。取值范围是 0 ~ 100。通常是零。只有 EngineBitmapDecodeInterceptor 可以是 100
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

    // 如果当前 DrawableDecodeInterceptor 会修改返回的结果并且仅用于部分请求，那么请给一个不重复的 key 用于构建缓存 key，否则给 null 即可
    override val key: String = "MyDrawableDecodeInterceptor"

    // 用于排序，值越大在列表中越靠后。取值范围是 0 ~ 100。通常是零。只有 EngineDrawableDecodeInterceptor 可以是 100
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

> 1. MyBitmapDecodeInterceptor 演示了一个将所有请求的 Bitmap.Config 改为 ARGB_4444 的案例
> 2. MyDrawableDecodeInterceptor 演示了一个禁止所有请求解码动图的案例
> 3. 如果你想修改返回结果，就拦截 proceed 方法返回的结果，返回一个新的 [BitmapDecodeResult] 或 [DrawableDecodeResult] 即可
> 4. 如果想不再执行请求只需不执行 proceed 方法即可

然后在初始化 Sketch 时通过 addBitmapDecodeInterceptor() 和 addDrawableDecodeInterceptor() 方法注册，这样所有的
ImageRequest 都可以使用，如下：

```kotlin
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
```

或者在显示图片时只给当前 [ImageRequest] 注册，这样就只有当前 [ImageRequest] 可以使用，如下：

```kotlin
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