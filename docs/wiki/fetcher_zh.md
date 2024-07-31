# Fetcher

翻译：[English](fetcher.md)

[Fetcher] 用于从 uri 获取数据，返回 [FetchResult]，交由 [Decoder] 使用。

[Sketch] 支持的每一种 uri 都有对应的 [Fetcher] 实现，如下表所示：

| URI                      | Fetcher                     | Create                  | Dependent modules        | Android | iOS | Desktop | Web |
|:-------------------------|-----------------------------|-------------------------|--------------------------|---------|:----|:--------|:----|
| http://, https://        | [HttpUriFetcher]            | -                       | -                        | ✅       | ✅   | ✅       | ✅   |
| file://, /               | [FileUriFetcher]            | newFileUri()            | -                        | ✅       | ✅   | ✅       | ✅   |
| file://compose_resource/ | [ComposeResourceUriFetcher] | newComposeResourceUri() | sketch-compose-resources | ✅       | ✅   | ✅       | ✅   |
| data:image/jpeg;base64   | [Base64UriFetcher]          | newBase64Uri()          | -                        | ✅       | ✅   | ✅       | ✅   |
| asset://                 | [AssetUriFetcher]           | newAssetUri()           | -                        | ✅       | ❌   | ❌       | ❌   |
| content://               | [ContentUriFetcher]         | -                       | -                        | ✅       | ❌   | ❌       | ❌   |
| android.resource://      | [ResourceUriFetcher]        | newResourceUri()        | -                        | ✅       | ❌   | ❌       | ❌   |
| app.icon://              | [AppIconUriFetcher]         | newAppIconUri()         | sketch-extensions-core   | ✅       | ❌   | ❌       | ❌   |
| file://kotlin_resource/  | [KotlinResourceUriFetcher]  | newKotlinResourceUri()  | -                        | ❌       | ✅   | ✅       | ❌   |

* [AssetUriFetcher] 用于从 Android 的 assets 目录加载图片
* [ContentUriFetcher] 用于 Android 的 ContentResolver 加载图片
* [ResourceUriFetcher] 用于从 Android 的 resources 目录加载图片
* [AppIconUriFetcher] 用于加载已安装 App 的图标，它还需要依赖 `sketch-extensions-core`
  模块。[了解更多](apk_app_icon_zh.md#加载已安装-App-的图标)
* [Base64UriFetcher] 用于从 uri 本身的 base64 数据块中加载图片
* [ComposeResourceUriFetcher] 用于从 Compose Multiplatform 的 composeResources
  目录加载图片，它还需要依赖 `sketch-compose-resources` 模块。
* [KotlinResourceUriFetcher] 用于从 kotlin 的 resources 目录加载图片

## 注册 Fetcher

需要依赖单独模块的 [Fetcher]（例如 [ComposeResourceUriFetcher]），需要在初始化 Sketch 时注册，如下：

```kotlin
// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addFetcher(ComposeResourceUriFetcher.Factory())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "https://www.example.com/image.gif") {
    components {
        addFetcher(ComposeResourceUriFetcher.Factory())
    }
}
```

### 扩展 Fetcher

先实现 [Fetcher] 接口定义你的 Fetcher 和它的 Factory，然后通过 addFetcher() 方法注册即可，如下：

```kotlin
class MyFetcher : Fetcher {

    override suspend fun fetch(): Result<FetchResult> {
        // 在这里解析你的 uri，获取数据
    }

    companion object {
        const val MY_SCHEME = "myUri"
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): MyFetcher? {
            return if (request.uri.startWith("$MY_SCHEME://")) {
                MyFetcher()
            } else {
                null
            }
        }
    }
}

// 在自定义 Sketch 时为所有 ImageRequest 注册
Sketch.Builder(context).apply {
    components {
        addFetcher(MyFetcher.Factory())
    }
}.build()

// 加载图片时为单个 ImageRequest 注册
ImageRequest(context, "myUri://sample.jpeg") {
    components {
        addFetcher(MyFetcher.Factory())
    }
}
```

[comment]: <> (classs)

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[Decoder]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/decode/Decoder.kt

[Fetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Fetcher.kt

[FetchResult]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FetchResult.kt

[AssetUriFetcher]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/AssetUriFetcher.kt

[Base64UriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/Base64UriFetcher.kt

[ContentUriFetcher]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/ContentUriFetcher.kt

[FileUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/FileUriFetcher.kt

[HttpUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[ResourceUriFetcher]: ../../sketch-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/ResourceUriFetcher.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/androidMain/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt

[KotlinResourceUriFetcher]: ../../sketch-core/src/desktopMain/kotlin/com/github/panpf/sketch/fetch/KotlinResourceUriFetcher.kt

[ComposeResourceUriFetcher]: ../../sketch-compose-resources/src/commonMain/kotlin/com/github/panpf/sketch/fetch/ComposeResourceUriFetcher.kt