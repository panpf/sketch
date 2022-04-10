# Fetcher

[Fetcher] 用于从 uri 获取数据，交由 [BitmapDecoder] 解码 Bitmap 或 [DrawableDecoder] 解码 Drawable。

[Sketch] 对支持的每一种 uri 都有对应的 [Fetcher] 实现，共有如下几种：

* [AssetUriFetcher][AssetUriFetcher]
* [Base64UriFetcher][Base64UriFetcher]
* [ContentUriFetcher][ContentUriFetcher]
* [FileUriFetcher][FileUriFetcher]
* [HttpUriFetcher][HttpUriFetcher]
* [ResourceUriFetcher][ResourceUriFetcher]
* [AppIconUriFetcher][AppIconUriFetcher]

## 扩展新的 Fetcher

1.首先需要实现 [Fetcher] 接口定义你的 [Fetcher] 和它的 Factory，如下：

```kotlin
class MyFetcher : Fetcher {

    override suspend fun fetch(): FetchResult {
        // 在这里解析你的 uri，获取数据
        TODO("implementation")
    }

    companion object {
        const val MY_SCHEME = "myUri"
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): MyFetcher? {
            return if (request.uriString.startWith(MY_SCHEME)) {
                MyFetcher()
            } else {
                null
            }
        }
    }
}
```

2.然后在配置 [Sketch] 时通过 components 方法将其 Factory 注册到 [Sketch]，如下：

```kotlin
class MyApplication : MultiDexApplication(), SketchConfigurator {

    override fun createSketchConfig(): Builder.() -> Unit = {
        components {
            addFetcher(MyFetcher.Factory())
        }
    }
}
```

[comment]: <> (class)

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[BitmapDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/BitmapDecoder.kt

[DrawableDecoder]: ../../sketch/src/main/java/com/github/panpf/sketch/decode/DrawableDecoder.kt

[Fetcher]: ../../sketch/src/main/java/com/github/panpf/sketch/fetch/Fetcher.kt

[AssetUriFetcher]: ../../sketch/src/main/java/com/github/panpf/sketch/fetch/AssetUriFetcher.kt

[Base64UriFetcher]: ../../sketch/src/main/java/com/github/panpf/sketch/fetch/Base64UriFetcher.kt

[ContentUriFetcher]: ../../sketch/src/main/java/com/github/panpf/sketch/fetch/ContentUriFetcher.kt

[FileUriFetcher]: ../../sketch/src/main/java/com/github/panpf/sketch/fetch/FileUriFetcher.kt

[HttpUriFetcher]: ../../sketch/src/main/java/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[ResourceUriFetcher]: ../../sketch/src/main/java/com/github/panpf/sketch/fetch/ResourceUriFetcher.kt

[AppIconUriFetcher]: ../../sketch-extensions/src/main/java/com/github/panpf/sketch/fetch/AppIconUriFetcher.kt