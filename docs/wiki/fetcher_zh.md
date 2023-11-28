# Fetcher

翻译：[English](fetcher.md)

[Fetcher] 用于从 uri 获取数据，返回 [FetchResult]，交由 [BitmapDecoder] 或 [DrawableDecoder] 使用。

Sketch 对支持的每一种 uri 都有对应的 [Fetcher] 实现，共有如下几种：

* [AssetUriFetcher][AssetUriFetcher]：从 app 的 assets 目录加载图片
* [Base64UriFetcher][Base64UriFetcher]：从 uri 本身加载 base 64 格式的图片
* [ContentUriFetcher][ContentUriFetcher]：从 ContentResolver 加载图片
* [FileUriFetcher][FileUriFetcher]：从本地文件加载图片
* [HttpUriFetcher][HttpUriFetcher]：从 http uri 加载图片
* [ResourceUriFetcher][ResourceUriFetcher]：从 Android Resource 中加载图片
* [AppIconUriFetcher][AppIconUriFetcher]：从已安装 app
  加载其图标，[了解更多](apk_app_icon_zh.md#显示已安装-APP-的图标)

### 扩展新的 Fetcher

首先需要实现 [Fetcher] 接口定义你的 [Fetcher] 和它的 Factory，如下：

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
          return if (request.uriString.startWith("$MY_SCHEME://")) {
                MyFetcher()
            } else {
                null
            }
        }
    }
}
```

然后通过 addFetcher 方法注册，如下：

```kotlin
/* 为所有 ImageRequest 注册 */
class MyApplication : Application(), SketchFactory {

    override fun createSketch(): Sketch {
        return Sketch.Builder(this).apply {
            components {
                addFetcher(MyFetcher.Factory())
            }
        }.build()
    }
}

/* 为单个 ImageRequest 注册 */
imageView.displayImage(context, "myUri://sample.jpeg") {
    components {
        addFetcher(MyFetcher.Factory())
    }
}
```

[comment]: <> (class)

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[BitmapDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/BitmapDecoder.kt

[DrawableDecoder]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/decode/DrawableDecoder.kt

[Fetcher]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/fetch/Fetcher.kt

[FetchResult]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/fetch/FetchResult.kt

[AssetUriFetcher]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/fetch/AssetUriFetcher.kt

[Base64UriFetcher]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/fetch/Base64UriFetcher.kt

[ContentUriFetcher]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/fetch/ContentUriFetcher.kt

[FileUriFetcher]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/fetch/FileUriFetcher.kt

[HttpUriFetcher]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[ResourceUriFetcher]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/fetch/ResourceUriFetcher.kt

[AppIconUriFetcher]: ../../sketch-extensions-core/src/main/kotlin/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt