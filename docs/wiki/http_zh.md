# HttpStack

翻译：[English](http.md)

[HttpStack] 用来发起 HTTP 网络请求并获取响应然后交由 [HttpUriFetcher] 下载图片

在 jvm 平台上，[Sketch] 提供了 [HurlStack] 和 [OkHttpStack]
两种实现，默认使用 [HurlStack]，[OkHttpStack] 需要额外依赖 `sketch-http-okhttp` 模块

在非 jvm 平台上 [Sketch] 只提供了 [KtorStack] 实现，默认使用 [KtorStack]。

你也可以在 jvm 平台上使用 [KtorStack]，这需要额外依赖 `sketch-http-ktor` 模块

### HurlStack

[HurlStack] 采用 HttpURLConnection 实现，支持以下配置：

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(context).apply {
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

                // HttpURLConnection 在 执行 connect 之前交由此方法处理一下。默认 null
                processRequest { url: String, connection: HttpURLConnection ->

                }
            }.build())
        }.build()
    }
}
```

### OkHttpStack

使用 [OkHttpStack] 之前需要先依赖 `sketch-http-okhttp` 模块，然后在初始化 [Sketch] 时通过 `httpStack()`
方法注册即可，如下：

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(context).apply {
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
}
```

### KtorStack

在 jvm 平台上使用 [KtorStack] 之前需要先依赖 `sketch-http-ktor` 模块，然后在初始化 [Sketch]
时通过 `httpStack()` 方法注册即可，如下：

```kotlin
Sketch.Builder(context).apply {
    httpStack(KtorStack())
}.build()
```

### 自定义

首先实现 [HttpStack] 接口定义自己的 [HttpStack]，然后在初始化 [Sketch] 时通过 `httpStack()` 方法注册即可：

```kotlin
Sketch.Builder(context).apply {
    httpStack(MyHttpStack())
}.build()
```

[HttpStack]: ../../sketch-http-core/src/commonMain/kotlin/com/github/panpf/sketch/http/HttpStack.kt

[HurlStack]: ../../sketch-http-core/src/jvmCommonMain/kotlin/com/github/panpf/sketch/http/HurlStack.kt

[OkHttpStack]: ../../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/http/OkHttpStack.kt

[KtorStack]: ../../sketch-http-ktor/src/commonMain/kotlin/com/github/panpf/sketch/http/KtorStack.kt

[HttpUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt