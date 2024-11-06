# Http 网络图片

翻译：[English](http.md)

## 组件

Sketch 提供了 `sketch-http-*` 系列模块以支持 Http 网络图片，所支持的平台以及差异如下：

| Module             | FetcherProvider                                                            | Fetcher                                                                         | Android | iOS | Desktop | Js | WasmJs |
|:-------------------|:---------------------------------------------------------------------------|:--------------------------------------------------------------------------------|:--------|:----|:--------|:---|--------|
| sketch-http        | jvm: [HurlHttpUriFetcherProvider]<br/>nonJvm: [KtorHttpUriFetcherProvider] | jvm: [HurlHttpUriFetcher]<br/>nonJvm: [KtorHttpUriFetcher][Ktor3HttpUriFetcher] | ✅       | ✅   | ✅       | ✅  | ✅      |
| sketch-http-hurl   | [HurlHttpUriFetcherProvider]                                               | [HurlHttpUriFetcher]                                                            | ✅       | ❌   | ✅       | ❌  | ❌      |
| sketch-http-okhttp | [OkHttpHttpUriFetcherProvider]                                             | [OkHttpHttpUriFetcher]                                                          | ✅       | ❌   | ✅       | ❌  | ❌      |
| sketch-http-ktor2  | [KtorHttpUriFetcherProvider][Ktor2HttpUriFetcherProvider]                  | [KtorHttpUriFetcher][Ktor2HttpUriFetcher]                                       | ✅       | ✅   | ✅       | ✅  | ❌      |
| sketch-http-ktor3  | [KtorHttpUriFetcherProvider][Ktor3HttpUriFetcherProvider]                  | [KtorHttpUriFetcher][Ktor3HttpUriFetcher]                                       | ✅       | ✅   | ✅       | ✅  | ✅      |

> [!IMPORTANT]
> * HurlHttpUriFetcher 使用 jvm 自带的 HttpURLConnection 实现，不需要额外的依赖
> * `sketch-http-ktor2` 和 `sketch-http-ktor3` 模块都包含各个平台所需的引擎，如果你需要使用其它引擎请使用它们的
    core 版本，例如 `sketch-http-ktor2-core` 和 `sketch-http-ktor3-core`，然后配置自己所需的引擎的依赖
> * ktor2 不支持 wasmJs 平台，必须要支持 wasmJs 平台的请使用 ktor3
> * 上述组件都支持自动注册，你只需要导入即可，无需额外配置，如果你需要手动注册，
    请阅读文档：[《注册组件》](register_component_zh.md)

## 安装组件

加载网络图片前需要先从上述组件中选择一个并安装依赖，以 `sketch-http` 为例：

`${LAST_VERSION}`: [![Download][version_icon]][version_link] (不包含 'v')

```kotlin
implementation("io.github.panpf.sketch4:sketch-http:${LAST_VERSION}")
```

## 加载网络图片

直接使用 http uri 加载图片即可，如下：

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

## 配置

Sketch 将 http 部分抽象为 [HttpStack]，每一个 \*HttpUriFetcher 都有对应的 [HttpStack] 实现，如下：

* HurlHttpUriFetcher：[HurlStack]
* OkHttpHttpUriFetcher：[OkHttpStack]
* KtorHttpUriFetcher：[KtorStack]

你可以先禁用相关组件的自动注册，然后在手动配置 \*HttpUriFetcher 时修改 [HttpStack] 的配置，如下：

HurlStack:

```kotlin
Sketch.Builder(context).apply {
    addIgnoreFetcherProvider(HurlHttpUriFetcherProvider::class)
    addComponents {
        val httpStack = HurlStack.Builder().apply {
            connectTimeout(5000)
            readTimeout(5000)
            userAgent("Android 8.1")
            headers("accept-encoding" to "gzip")   // 不可重复的 header
            addHeaders("cookie" to "...")    // 可重复的 header
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
            headers("accept-encoding" to "gzip")   // 不可重复的 header
            addHeaders("cookie" to "...")    // 可重复的 header
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

[KtorStack]: ../../sketch-http-ktor3-core/src/commonMain/kotlin/com/github/panpf/sketch/http/KtorStack.kt

[HttpUriFetcher]: ../../sketch-http-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[HurlHttpUriFetcher]: ../../sketch-http-hurl/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HurlHttpUriFetcher.kt

[OkHttpHttpUriFetcher]: ../../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/fetch/OkHttpHttpUriFetcher.kt

[Ktor2HttpUriFetcher]: ../../sketch-http-ktor2-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/KtorHttpUriFetcher.kt

[Ktor3HttpUriFetcher]: ../../sketch-http-ktor3-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/KtorHttpUriFetcher.kt

[HurlHttpUriFetcherProvider]: ../../sketch-http-hurl/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/HurlHttpUriFetcherProvider.kt

[OkHttpHttpUriFetcherProvider]: ../../sketch-http-okhttp/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/OkHttpHttpUriFetcherProvider.kt

[Ktor2HttpUriFetcherProvider]: ../../sketch-http-ktor2-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/KtorHttpUriFetcherProvider.common.kt

[Ktor3HttpUriFetcherProvider]: ../../sketch-http-ktor3-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/internal/KtorHttpUriFetcherProvider.common.kt