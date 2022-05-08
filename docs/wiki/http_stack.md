# HttpStack

[HttpStack] 用来发起 HTTP 网络请求并获取响应然后交由 [HttpUriFetcher] 下载图片，默认的实现是 [HurlStack]

### HurlStack

[HurlStack] 采用 HttpURLConnection 实现，支持以下配置：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        httpStack(HurlStack.Builder().apply {
            // 连接超时。默认 7000
            connectTimeout(Int)

            // 读取超时。默认 7000
            readTimeout(Int)

            // User-Agent。默认 null
            userAgent(String)

            // 添加一些不可重复的 header。默认 null
            extraHeaders(Map<String, String>)

            // 添加一些可重复的 header。默认 null
            addExtraHeaders(Map<String, String>)

            // HttpURLConnection 在 执行 connect 直线交由此方法处理一下。默认 null
            processRequest { url: String, connection: HttpURLConnection ->

            }
        }.build())
    }.build()
}
```

### OkHttpStack

Sketch 还提供了 [HttpStack] 的 [OkHttpStack] 实现，使用之前需要先导入 `sketch-okhttp` 模块，然后在初始化 Sketch 时通过 httpStack()
方法注册即可，如下：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        httpStack(OkHttpStack.Builder().apply {
            // 连接超时。默认 7000
            connectTimeout(Int)

            // 读取超时。默认 7000
            readTimeout(Int)

            // User-Agent。默认 null
            userAgent(String)

            // 添加一些不可重复的 header。默认 null
            extraHeaders(Map<String, String>)

            // 添加一些可重复的 header。默认 null
            addExtraHeaders(Map<String, String>)

            // 拦截器。默认 null
            interceptors(Interceptor)

            // 网络拦截器。默认 null
            networkInterceptors(Interceptor)
        }.build())
    }.build()
}
```

> 注意：由于需要兼容 Android 4.1 所以使用的是较旧的 3.12.0 版本的 OkHttp，如果你的 app 最低版本较高，那么你可以使用较新版本的 OkHttp 自定一个 HttpStack

### 自定义：

实现 [HttpStack] 接口定义自己的 HttpStack，然后在初始化 Sketch 时通过 httpStack() 方法注册即可：

```kotlin
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        httpStack(MyHttpStack())
    }.build()
}
```

[HttpStack]: ../../sketch/src/main/java/com/github/panpf/sketch/http/HttpStack.kt

[HurlStack]: ../../sketch/src/main/java/com/github/panpf/sketch/http/HurlStack.kt

[OkHttpStack]: ../../sketch-okhttp/src/main/java/com/github/panpf/sketch/http/OkHttpStack.kt

[HttpUriFetcher]: ../../sketch/src/main/java/com/github/panpf/sketch/fetch/HttpUriFetcher.kt