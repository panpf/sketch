# DecodeInterceptor

翻译：[English](decode_interceptor.md)

Sketch 的解码过程支持通过拦截器来改变解码前后的输入和输出

先实现 [DecodeInterceptor] 接口实现你的 DecodeInterceptor，然后通过 addDecodeInterceptor() 方法注册即可，如下：

```kotlin
class MyDecodeInterceptor : DecodeInterceptor {

    // 如果当前 DecodeInterceptor 会修改返回的结果并且仅用于部分请求，那么请给一个不重复的 key 用于构建缓存 key，否则给 null 即可
    override val key: String = "MyDecodeInterceptor"

    // 用于排序，值越大在列表中越靠后。取值范围是 0 ~ 100。通常是零。只有 EngineDecodeInterceptor 可以是 100
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

// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "https://example.com/image.jpg") {
    components {
        addDecodeInterceptor(MyDecodeInterceptor())
    }
}
```

> [!TIP]
> 1. MyDecodeInterceptor 演示了一个将所有请求的 Bitmap.Config 改为 RGB_565 的案例
> 2. 如果你想修改返回结果，就拦截 proceed 方法返回的结果，返回一个新的 [DecodeResult] 即可
> 3. 如果想不再执行请求只需不执行 proceed 方法即可

[comment]: <> (classs)

[DecodeInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/DecodeInterceptor.kt

[DecodeResult]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/DecodeResult.kt