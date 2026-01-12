# 结果缓存

翻译：[English](result_cache.md)

为了避免重复转换图片并提高图片的加载速度 [Sketch] 引入了结果缓存，结果缓存功能会将转换后的图片持久的存储在磁盘上，下次直接从磁盘读取跳过转换过程。

结果缓存功能由 [ResultCacheRequestInterceptor] 负责核心逻辑，[DiskCache] 负责存储管理

[DiskCache] 的默认实现是 [LruDiskCache]：

* 默认最大容量是 200 MB
* 根据最少使用原则清除旧的缓存

## 缓存目录

为了适应不同平台的差异，所以在不同平台上缓存目录的位置也不一样

### Android

在 Android 上默认的结果缓存目录按以下顺序获取：

1. `/sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch4/result`
2. `/data/data/[APP_PACKAGE_NAME]/cache/sketch4/result`

> [!TIP]
> 为了兼容多进程，在非主进程使用 Sketch 时缓存目录名称后会加上进程名，例如 "result:push"

### iOS

在 iOS 上默认的结果缓存目录是：

```kotlin
val appCacheDirectory =
    NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true).first() as String
val resultCacheDir = "$appCacheDirectory/sketch4/result"
```

### Desktop

在桌面平台上默认的结果缓存目录是：

```kotlin
val appName = (getComposeResourcesPath() ?: getJarPath(Sketch::class.java)).md5()

// macOS
"/Users/[user]/Library/Caches/SketchImageLoader/${appName}/sketch4/result"

// Windows
"C:\\Users\\[user]\\AppData\\Local\\SketchImageLoader\\${appName}\\sketch4/result\\Cache"

// Linux
"/home/[user]/.cache/SketchImageLoader/${appName}/sketch4/result"
```

### Web

Web 平台尚不支持结果缓存

## 自定义

你可以在初始化 [Sketch] 时通过 [Sketch].Builder 的 resultCache() 或 resultCacheOptions()
方法自定义结果缓存的实现或配置，如下：

```kotlin
// 使用默认的 LruDiskCache 实现并配置其参数
Sketch.Builder(context).apply {
    resultCacheOptions(
        DiskCache.Options(
            // directory 和 appCacheDirectory 二选一即可
            directory = "/tmp/myapp/sketch/result",
            // directory 和 appCacheDirectory 二选一即可
            appCacheDirectory = "/tmp/myapp",
            // 100 MB
            maxSize = 1024 * 1024 * 100,
            // app 对结果缓存的管理版本号，如果想清除旧的结果缓存就升级此版本号
            appVersion = 1,
        )
    )
}.build()

// 使用你自己的 DiskCache 实现
class MyDiskCache : DiskCache {
    // ...
}
Sketch.Builder(context).apply {
    resultCache(MyDiskCache())
}.build()
```

## 缓存策略

结果缓存策略用于控制如何使用结果缓存，默认配置是 [CachePolicy].ENABLED，你可以通过 [ImageRequest]
或 [ImageOptions] 的 resultCachePolicy
属性配置它:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // 禁用
    resultCachePolicy(CachePolicy.DISABLED)
    // 只读
    resultCachePolicy(CachePolicy.READ_ONLY)
    // 只写
    resultCachePolicy(CachePolicy.WRITE_ONLY)
} 
```

## 缓存 key

默认情况下 Sketch 会自动根据请求的配置生成结果缓存 key，但你还可以通过以下属性自定义结果缓存 key：

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // 使用自定义的结果缓存 key
    resultCacheKey("https://example.com/image.jpg?width=100&height=100")

    // 修改自动生成的结果缓存 key
    resultCacheKeyMapper(CacheKeyMapper { "${it}&width=100&height=100" })
}

ImageOptions {
    // 使用自定义的结果缓存 key
    resultCacheKey("https://example.com/image.jpg?width=100&height=100")

    // 修改自动生成的结果缓存 key
    resultCacheKeyMapper(CacheKeyMapper { "${it}&width=100&height=100" })
}
```

你还可以通过以下方式和获取最终的结果缓存 key：

```kotlin
// 在自定义的 RequestInterceptor、Transformation、Fetcher、Decoder 组件中
// 可以通过 RequestContext 获取结果缓存 key
val requestContext: RequestContext = ...
requestContext.resultCacheKey

// 从 ImageResult 中获取结果缓存 key
val imageSuccess = sketch.execute(request) as ImageResult.Success
imageSuccess.resultCacheKey
```

## 读写缓存

你可以通过 `sketch.resultCache` 属性获取结果缓存实例来访问结果缓存，但要注意先获取锁再访问，这样能避免在多线程下出问题，如下：

```kotlin
scope.launch {
    val resultCache = sketch.resultCache
    val resultCacheKey = requestContext.resultCacheKey
    resultCache.withLock(resultCacheKey) {
        // get
        openSnapshot(resultCacheKey)?.use { snapshot ->
            val dataPath: Path = snapshot.data
            val metadataPath: Path = snapshot.metadata
            val dataContent = fileSystem.source(dataPath).buffer().use {
                it.readUtf8()
            }
            val metadataContent = fileSystem.source(metadataPath).buffer().use {
                it.readUtf8()
            }
        }

        // edit
        val editor: DiskCache.Editor? = openEditor(resultCacheKey)
        if (editor != null) {
            try {
                val dataPath: Path = editor.data
                val metadataPath: Path = editor.metadata
                fileSystem.sink(dataPath).buffer().use {
                    it.writeUtf8("data")
                }
                fileSystem.sink(metadataPath).buffer().use {
                    it.writeUtf8("metadata")
                }
                editor.commit()
            } catch (e: Exception) {
                editor.abort()
            }
        }

        // remove
        val cleared: Boolean = remove(resultCacheKey)
    }

    // Clear all
    resultCache.clear()
}
```

> [!CAUTION]
> 1. 同一个 key 的 openSnapshot 和 openEditor 是互相冲突的，例如 openSnapshot 未关闭前 openEditor
     始终返回 null，反之亦然
> 2. 所以一定要在 withLock 里面执行，否则可能会出现意外

更多可用方法请参考 [DiskCache]

## 清除缓存

结果缓存会在以下几种情况下清除：

1. 主动调用 [DiskCache] 的 `remove()`、clear()` 方法
2. 主动调用 [DiskCache].Editor 的 `abort()` 方法
3. 达到最大容量时自动清除较旧的缓存

[Sketch]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[DiskCache]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/DiskCache.common.kt

[LruDiskCache]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/LruDiskCache.common.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[ResultCacheRequestInterceptor]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/ResultCacheRequestInterceptor.kt

[CachePolicy]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/CachePolicy.kt