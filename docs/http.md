# Http network image

Translations: [简体中文](http.zh.md)

## Components

Sketch provides the `sketch-http-*` series of modules to support Http network images, the
supported platforms and differences are as follows:

| Module             | ComponentProvider                                                                                    | Fetcher                                                                         | Android | iOS | Desktop | Js | WasmJs |
|:-------------------|:-----------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------|:--------|:----|:--------|:---|--------|
| sketch-http        | jvm: [HurlHttpComponentProvider]<br/>nonJvm: [KtorHttpComponentProvider][Ktor3HttpComponentProvider] | jvm: [HurlHttpUriFetcher]<br/>nonJvm: [KtorHttpUriFetcher][Ktor3HttpUriFetcher] | ✅       | ✅   | ✅       | ✅  | ✅      |
| sketch-http-hurl   | [HurlHttpComponentProvider]                                                                          | [HurlHttpUriFetcher]                                                            | ✅       | ❌   | ✅       | ❌  | ❌      |
| sketch-http-okhttp | [OkHttpHttpComponentProvider]                                                                        | [OkHttpHttpUriFetcher]                                                          | ✅       | ❌   | ✅       | ❌  | ❌      |
| sketch-http-ktor2  | [KtorHttpComponentProvider][Ktor2HttpComponentProvider]                                              | [KtorHttpUriFetcher][Ktor2HttpUriFetcher]                                       | ✅       | ✅   | ✅       | ✅  | ❌      |
| sketch-http-ktor3  | [KtorHttpComponentProvider][Ktor3HttpComponentProvider]                                              | [KtorHttpUriFetcher][Ktor3HttpUriFetcher]                                       | ✅       | ✅   | ✅       | ✅  | ✅      |

> [!IMPORTANT]
> * HurlHttpUriFetcher is implemented using jvm’s own HttpURLConnection and does not require
    additional dependencies.
> * Both the `sketch-http-ktor2` and `sketch-http-ktor3` modules contain the engines required for
    each platform. If you need to use other engines, please use their core versions, such as
    `sketch-http-ktor2-core` and ` sketch-http-ktor3-core`, and then configure the dependencies of
    the engine you need
> * ktor2 does not support the wasmJs platform. If you must support the wasmJs platform, please use
    ktor3
> * The above components all support automatic registration. You only need to import them without
    additional configuration. If you need to register manually, please read the
    documentation: [《Register component》](register_component.md)

## Install component

Before loading network images, you need to select one of the above components and configure
dependencies. Take `sketch-http` as an example:

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (Not included 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-http:${LAST_VERSION}")
```

## Load network images

Simply use http uri to load images, as follows:

```kotlin
val imageUri = "https://www.sample.com/image.jpg"

// compose
AsyncImage(
    uri = imageUri,
    contentDescription = "photo"
)

// view
imageView.loadImage(imageUri)
```

## Configuration

Sketch abstracts the http part into [HttpStack], and each \*HttpUriFetcher has a
corresponding [HttpStack] implementation, as follows:

* HurlHttpUriFetcher：[HurlStack]
* OkHttpHttpUriFetcher：[OkHttpStack]
* KtorHttpUriFetcher：[KtorStack]

You can disable automatic registration of related components first, and then modify the
configuration of [HttpStack] when manually configuring \*HttpUriFetcher, as follows:

HurlStack:

```kotlin
Sketch.Builder(context).apply {
    addIgnoredComponentProvider(HurlHttpComponentProvider::class)
    addComponents {
        val httpStack = HurlStack.Builder().apply {
            connectTimeout(5000)
            readTimeout(5000)
            userAgent("Android 8.1")
            headers("accept-encoding" to "gzip")   // non-repeatable header
            addHeaders("cookie" to "...")    // repeatable header
            addInterceptor(object : HurlStack.Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val connection: HttpURLConnection = chain.connection
                    // ...
                    return chain.proceed()
                }
            })
        }.build()
        addFetcher(HurlHttpUriFetcher.Factory(httpStack))
    }
}.build()
```

OkHttpStack:

```kotlin
Sketch.Builder(context).apply {
    addIgnoredComponentProvider(OkHttpHttpComponentProvider::class)
    addComponents {
        val httpStack = OkHttpStack.Builder().apply {
            connectTimeout(5000)
            readTimeout(5000)
            userAgent("Android 8.1")
            headers("accept-encoding" to "gzip")   // non-repeatable header
            addHeaders("cookie" to "...")    // repeatable header
            interceptors(object : okhttp3.Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                    // ...
                    return chain.proceed(request)
                }
            })
            networkInterceptors(object : okhttp3.Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                    // ...
                    return chain.proceed(request)
                }
            })
        }.build()
        addFetcher(OkHttpHttpUriFetcher.Factory(httpStack))
    }
}.build()
```

KtorStack:

```kotlin
Sketch.Builder(context).apply {
    addIgnoredComponentProvider(KtorHttpComponentProvider::class)
    addComponents {
        val httpClient = HttpClient {
            // ...
        }
        val httpStack = KtorStack(httpClient)
        addFetcher(KtorHttpUriFetcher.Factory(httpStack))
    }
}.build()
```

[comment]: <> (classs)

[version_icon]: https://img.shields.io/maven-central/v/io.github.panpf.sketch4/sketch-singleton

[version_link]: https://repo1.maven.org/maven2/io/github/panpf/sketch4/

[HttpStack]: ../sketch-http-core/src/commonMain/kotlin/com/github/panpf/sketch/http/HttpStack.kt

[HurlStack]: ../sketch-http-hurl/src/commonMain/kotlin/com/github/panpf/sketch/http/HurlStack.kt

[OkHttpStack]: ../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/http/OkHttpStack.kt

[HttpUriFetcher]: ../sketch-http-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[HurlHttpUriFetcher]: ../sketch-http-hurl/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HurlHttpUriFetcher.kt

[OkHttpHttpUriFetcher]: ../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/fetch/OkHttpHttpUriFetcher.kt

[Ktor2HttpUriFetcher]: ../sketch-http-ktor2-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/KtorHttpUriFetcher.kt

[Ktor3HttpUriFetcher]: ../sketch-http-ktor3-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/KtorHttpUriFetcher.kt

[HurlHttpComponentProvider]: ../sketch-http-hurl/src/commonMain/kotlin/com/github/panpf/sketch/util/HurlHttpComponentProvider.kt

[OkHttpHttpComponentProvider]: ../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/util/OkHttpHttpComponentProvider.kt

[Ktor2HttpComponentProvider]: ../sketch-http-ktor2-core/src/commonMain/kotlin/com/github/panpf/sketch/util/KtorHttpComponentProvider.kt

[Ktor3HttpComponentProvider]: ../sketch-http-ktor3-core/src/commonMain/kotlin/com/github/panpf/sketch/util/KtorHttpComponentProvider.kt
