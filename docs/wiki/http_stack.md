# HttpStack

Translations: [简体中文](http_stack_zh.md)

[HttpStack] is used to initiate HTTP network requests and obtain responses, then hand them over
to [HttpUriFetcher] to download images.

On the jvm platform, [Sketch] provides two implementations [HurlStack]
and [OkHttpStack]. [HurlStack] is used by default. [OkHttpStack] requires additional dependence on
the `sketch-http-okhttp` module.

On non-jvm platforms, [Sketch] only provides [KtorStack] implementation, and [KtorStack] is used by
default.

You can also use [KtorStack] on the jvm platform, which requires additional dependency on
the `sketch-http-ktor` module

### HurlStack

[HurlStack] is implemented using HttpURLConnection and supports the following configurations:

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(context).apply {
            httpStack(HurlStack.Builder().apply {
                // Connection timed out. Default 7000
                connectTimeout(Int)

                // Read timeout. Default 7000
                readTimeout(Int)

                // User-Agent. Default null
                userAgent(String)

                // Add some non-repeatable headers. Default null
                extraHeaders(Map<String, String>)

                // Add some repeatable headers. Default null
                addExtraHeaders(Map<String, String>)

                // HttpURLConnection is handled by this method before executing connect. Default null
                processRequest { url: String, connection: HttpURLConnection ->

                }
            }.build())
        }.build()
    }
}
```

### OkHttpStack

Before using [OkHttpStack], you need to rely on the `sketch-okhttp` module, and then
pass `httpStack()` when initializing [Sketch] The method to register is as follows:

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(context).apply {
            httpStack(OkHttpStack.Builder().apply {
                // Connection timed out. Default 7000
                connectTimeout(Int)

                // Read timeout. Default 7000
                readTimeout(Int)

                // User-Agent. Default null
                userAgent(String)

                // Add some non-repeatable headers. Default null
                extraHeaders(Map<String, String>)

                // Add some repeatable headers. Default null
                addExtraHeaders(Map<String, String>)

                // Interceptor. Default null
                interceptors(Interceptor)

                // Network blocker. Default null
                networkInterceptors(Interceptor)
            }.build())
        }.build()
    }
}
```

### KtorStack

Before using [KtorStack] on the jvm platform, you need to rely on the `sketch-http-ktor` module, and
then initialize [Sketch] You can register through the `httpStack()` method, as follows:

```kotlin
Sketch.Builder(context).apply {
    httpStack(KtorStack())
}.build()
```

### Customize

First implement the [HttpStack] interface to define your own [HttpStack], and then register it
through the `httpStack()` method when initializing [Sketch]:

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