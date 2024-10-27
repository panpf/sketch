# Http network image

Translations: [简体中文](http_zh.md)

## Components

Sketch provides the `sketch-http-*` series of modules to support Http network images, the
supported platforms and differences are as follows:

| Module             | FetcherProvider                                                            | Fetcher                                                                         | Android | iOS | Desktop | Web |
|:-------------------|:---------------------------------------------------------------------------|:--------------------------------------------------------------------------------|:--------|:----|:--------|:----|
| sketch-http        | jvm: [HurlHttpUriFetcherProvider]<br/>nonJvm: [KtorHttpUriFetcherProvider] | jvm: [HurlHttpUriFetcher]<br/>nonJvm: [KtorHttpUriFetcher][Ktor3HttpUriFetcher] | ✅       | ✅   | ✅       | ✅   |
| sketch-http-hurl   | [HurlHttpUriFetcherProvider]                                               | [HurlHttpUriFetcher]                                                            | ✅       | ❌   | ✅       | ❌   |
| sketch-http-okhttp | [OkHttpHttpUriFetcherProvider]                                             | [OkHttpHttpUriFetcher]                                                          | ✅       | ❌   | ✅       | ❌   |
| sketch-http-ktor2  | [KtorHttpUriFetcherProvider][Ktor2HttpUriFetcherProvider]                  | [KtorHttpUriFetcher][Ktor2HttpUriFetcher]                                       | ✅       | ✅   | ✅       | ✅   |
| sketch-http-ktor3  | [KtorHttpUriFetcherProvider][Ktor3HttpUriFetcherProvider]                  | [KtorHttpUriFetcher][Ktor3HttpUriFetcher]                                       | ✅       | ✅   | ✅       | ✅   |

> [!IMPORTANT]
> * HurlHttpUriFetcher is implemented using jvm’s own HttpURLConnection and does not require
    additional dependencies.
> * Both the `sketch-http-ktor2` and `sketch-http-ktor3` modules contain the engines required for
    each platform. If you need to use other engines, please use their core versions, such as
    `sketch-http-ktor2-core` and ` sketch-http-ktor3-core`, and then configure the dependencies of
    the engine you need
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

> [!IMPORTANT]
> ktor2 originally did not support wasmJs, so the wasmJs versions of `sketch-http-ktor2` and
`sketch-http-ktor2-core` actually use the `3.0.0-wasm2` version, and the `3.0.0-wasm2` version only
> It is published to the private warehouse of jetbrains, so you need to configure the private
> warehouse of jetbrains, as follows:
>   ```kotlin
>   allprojects {
>     repositories {
>        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")   // ktor 3.0.0-wasm2
>     }
>   }
>   ```

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
    addIgnoreFetcherProvider(HurlHttpUriFetcherProvider::class)
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
    addIgnoreFetcherProvider(OkHttpHttpUriFetcherProvider::class)
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
    addIgnoreFetcherProvider(KtorHttpUriFetcherProvider::class)
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

[HttpStack]: ../../sketch-http-core/src/commonMain/kotlin/com/github/panpf/sketch/http/HttpStack.kt

[HurlStack]: ../../sketch-http-hurl/src/commonMain/kotlin/com/github/panpf/sketch/http/HurlStack.kt

[OkHttpStack]: ../../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/http/OkHttpStack.kt

[HttpUriFetcher]: ../../sketch-http-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[HurlHttpUriFetcher]: ../../sketch-http-hurl/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HurlHttpUriFetcher.kt

[OkHttpHttpUriFetcher]: ../../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/fetch/OkHttpHttpUriFetcher.kt

[Ktor2HttpUriFetcher]: ../../sketch-http-ktor2-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/KtorHttpUriFetcher.kt

[Ktor3HttpUriFetcher]: ../../sketch-http-ktor3-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/KtorHttpUriFetcher.kt

[HurlHttpUriFetcherProvider]: ../../sketch-http-hurl/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/HurlHttpUriFetcherProvider.kt

[OkHttpHttpUriFetcherProvider]: ../../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/OkHttpHttpUriFetcherProvider.kt

[Ktor2HttpUriFetcherProvider]: ../../sketch-http-ktor2-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/KtorHttpUriFetcherProvider.common.kt

[Ktor3HttpUriFetcherProvider]: ../../sketch-http-ktor3-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/KtorHttpUriFetcherProvider.common.kt