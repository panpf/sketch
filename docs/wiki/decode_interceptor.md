# DecodeInterceptor

Sketch 通过 [DecodeInterceptor] 来拦截解码过程，你可以借此改变解码前后的输入和输出

Sketch 将解码分为 Drawable 和 Bitmap 两种，因此拦截也同样分为两种，如下：

```kotlin
class MyBitmapDecodeInterceptor : DecodeInterceptor<BitmapDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val newRequest = chain.request.newRequest {
            bitmapConfig(Bitmap.Config.ARGB_4444)
        }
        return chain.proceed(newRequest)
    }
}

class MyDrawableDecodeInterceptor : DecodeInterceptor<DrawableDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<DrawableDecodeResult>,
    ): DrawableDecodeResult {
        val newRequest = chain.request.newRequest {
            disabledAnimatedImage()
        }
        return chain.proceed(newRequest)
    }
}
```

> 1. MyBitmapDecodeInterceptor 演示了一个将所有请求的 Bitmap.Config 改为 ARGB_4444 的案例
> 2. MyDrawableDecodeInterceptor 演示了一个禁止所有请求解码动图的案例
> 3. 如果你想修改返回结果，就拦截 proceed 方法返回的结果，返回一个新的 [BitmapDecodeResult] 或 [DrawableDecodeResult] 即可
> 4. 如果想不再执行请求只需不执行 proceed 方法即可

然后在初始化 Sketch 时通过 addBitmapDecodeInterceptor() 和 addDrawableDecodeInterceptor() 方法注册即可：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        addBitmapDecodeInterceptor(MyBitmapDecodeInterceptor())
        addDrawableDecodeInterceptor(MyDrawableDecodeInterceptor())
    }.build()
}
```

[DecodeInterceptor]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/DecodeInterceptor.kt

[DecodeResult]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/DecodeResult.kt

[BitmapDecodeResult]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/BitmapDecodeResult.kt

[DrawableDecodeResult]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/DrawableDecodeResult.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt