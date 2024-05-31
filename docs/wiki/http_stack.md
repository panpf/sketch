# HttpStack

Translations: [简体中文](http_stack_zh.md)

[HttpStack] is used to initiate HTTP network requests and obtain responses and then hand them over
to [HttpUriFetcher] to download images. The default implementation is [HurlStack]

### HurlStack

[HurlStack] is implemented using HttpURLConnection and supports the following configurations:

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            httpStack(HurlStack.Builder().apply {
                // Connection timed out. Default 7000
                connectTimeout(Int)

                // Read timeout. Default 7000
                readTimeout(Int)

                // User-Agent. Default null
                userAgent(String)

                // Add non-repeatable headers. Default null
                extraHeaders(Map<String, String>)

                // Add repeatable headers. Default null
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

Sketch also provides [OkHttpStack] implementation of [HttpStack]. Before using it, you need to
import the `sketch-okhttp` module and then initialize it.
You can register through the `httpStack()` method when using Sketch, as follows:

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            httpStack(OkHttpStack.Builder().apply {
                // Connection timed out. Default 7000
                connectTimeout(Int)

                // Read timeout. Default 7000
                readTimeout(Int)

                // User-Agent. Default null
                userAgent(String)

                // Add non-repeatable headers. Default null
                extraHeaders(Map<String, String>)

                // Add repeatable headers. Default null
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

> Note: Because it needs to be compatible with Android 4.1, the older 3.12.0 version of OkHttp is
> used. If your app has a higher minimum version, you can use a newer version of OkHttp to customize
> an HttpStack.

### Android 4.* TLS 1.1, 1.2 supported

Android versions 4.1 to 4.4 support TLS 1.1 and 1.2 but are not enabled by default. HurlStack and
OkHttpStack are enabled as follows:

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            httpStack(OkHttpStack.Builder().apply {
                if (VERSION.SDK_INT <= 19) {
                    enabledTlsProtocols("TLSv1.1", "TLSv1.2")
                }
            }.build())

            // or
            httpStack(HurlStack.Builder().apply {
                if (VERSION.SDK_INT <= 19) {
                    enabledTlsProtocols("TLSv1.1", "TLSv1.2")
                }
            }.build())
        }.build()
    }
}
```

### Customize

Implement the [HttpStack] interface to define your own HttpStack, and then register it through
the `httpStack()` method when initializing Sketch:

```kotlin
class MyApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            httpStack(MyHttpStack())
        }.build()
    }
}
```

[HttpStack]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/http/HttpStack.kt

[HurlStack]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/http/HurlStack.kt

[OkHttpStack]: ../../sketch-okhttp/src/main/kotlin/com/github/panpf/sketch/http/OkHttpStack.kt

[HttpUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt