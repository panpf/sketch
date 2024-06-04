# RequestInterceptor

翻译：[English](request_interceptor.md)

Sketch 通过 [RequestInterceptor] 来拦截 [ImageRequest] 的执行过程，你可以借此改变执行过程的输入和输出

首先实现 [RequestInterceptor] 接口定义你的 RequestInterceptor，如下：

```kotlin
class MyRequestInterceptor : RequestInterceptor {

    // 如果当前 RequestInterceptor 会修改返回的结果并且仅用于部分请求，那么请给一个不重复的 key 用于构建缓存 key，否则给 null 即可
    override val key: String? = null

    // 用于排序，值越大在列表中越靠后。取值范围是 0 ~ 100。通常是零。只有 EngineRequestInterceptor 可以是 100
    override val sortWeight: Int = 0

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        // 所有请求禁止使用内存缓存
        val newRequest = chain.request.newRequest {
            memoryCachePolicy(CachePolicy.DISABLED)
        }
        return chain.proceed(newRequest)
    }
}
```

> [!TIP]
> 1. MyRequestInterceptor 演示了一个禁止所有请求使用内存缓存的案例
> 2. 如果你想修改返回结果，就拦截 proceed 方法返回的结果，返回一个新的 [ImageData] 即可
> 3. 如果想不再执行请求只需不执行 proceed 方法即可

然后注册你的 RequestInterceptor，如下：

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(this).apply {
    components {
        addRequestInterceptor(MyRequestInterceptor())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
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