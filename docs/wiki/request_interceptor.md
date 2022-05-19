# RequestInterceptor

Sketch 通过 [RequestInterceptor] 来拦截 [ImageRequest] 的执行过程，你可以借此改变执行过程的输入和输出

先实现 [RequestInterceptor] 接口定义你的 RequestInterceptor：

```kotlin
class MyRequestInterceptor : RequestInterceptor {

    override suspend fun intercept(chain: Chain): ImageData {
        // 所有请求禁止使用内存缓存
        val newRequest = chain.request.newRequest {
            memoryCachePolicy(CachePolicy.DISABLED)
        }
        return chain.proceed(newRequest)
    }
}
```

> 1. MyRequestInterceptor 演示了一个禁止所有请求使用内存缓存的案例
> 2. 如果你想修改返回结果，就拦截 proceed 方法返回的结果，返回一个新的 [ImageData] 即可
> 3. 如果想不再执行请求只需不执行 proceed 方法即可

然后在初始化 Sketch 时通过 addRequestInterceptor() 方法注册即可：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        addRequestInterceptor(MyRequestInterceptor())
    }.build()
}
```

[RequestInterceptor]: ../../sketch/src/main/java/com/github/panpf/sketch/request/RequestInterceptor.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageResult]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageResult.kt

[ImageData]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageData.kt