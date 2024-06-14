# 下载缓存

翻译：[English](download_cache.md)

为了避免重复从网络下载图片并提高图片的加载速度 [Sketch] 引入了下载缓存，[HttpUriFetcher]
会先将图片持久的存储在磁盘上，再从磁盘读取

下载缓存由 [DiskCache] 组件提供服务，默认实现是 [LruDiskCache]：

* 默认最大容量是 300 MB
* 根据最少使用原则清除旧的缓存

## 缓存目录

为了适应不同平台的差异，所以在不同平台上缓存目录的位置也不一样

### Android

在 Android 上默认的下载缓存目录按以下顺序获取：

1. `/sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch4/download`
2. `/data/data/[APP_PACKAGE_NAME]/cache/sketch4/download`

> [!TIP]
> 为了兼容多进程，在非主进程使用 Sketch 时缓存目录名称后会加上进程名，例如 "download:push"

### iOS

在 iOS 上默认的下载缓存目录是：

```kotlin
val appCacheDirectory =
    NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true).first() as String
val downloadCacheDir = "$appCacheDirectory/sketch4/download"
```

### Desktop

在桌面平台上默认的下载缓存目录是：

```kotlin
val appName = (getComposeResourcesPath() ?: getJarPath(Sketch::class.java)).md5()

// macOS
"/Users/[user]/Library/Caches/SketchImageLoader/${appName}/sketch4/download"

// Windows
"C:\\Users\\[user]\\AppData\\Local\\SketchImageLoader\\${appName}\\sketch4/download\\Cache"

// Linux
"/home/[user]/.cache/SketchImageLoader/${appName}/sketch4/download"
```

### Web

Web 平台尚不支持下载缓存

## 自定义

你可以在初始化 [Sketch] 时通过 [Sketch].Builder 的 downloadCache() 或 downloadCacheOptions()
方法自定义下载缓存的实现或配置，如下：

```kotlin
// 使用默认的 LruDiskCache 实现并配置其参数
Sketch.Builder(context).apply {
    downloadCacheOptions(
        DiskCache.Options(
            // directory 和 appCacheDirectory 二选一即可
            directory = "/tmp/myapp/sketch/download",
            // directory 和 appCacheDirectory 二选一即可
            appCacheDirectory = "/tmp/myapp",
            // 100 MB
            maxSize = 1024 * 1024 * 100,
            // app 对下载缓存的管理版本号，如果想清除旧的下载缓存就升级此版本号
            appVersion = 1,
        )
    )
}.build()

// 使用你自己的 DiskCache 实现
class MyDiskCache : DiskCache {
    // ...
}
Sketch.Builder(context).apply {
    downloadCache(MyDiskCache())
}.build()
```

## 配置请求

下载缓存默认配置是 [CachePolicy].ENABLED，你可以通过 [ImageRequest] 或 [ImageOptions] 的 downloadCachePolicy
属性控制下载缓存，如下:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // 禁用
    downloadCachePolicy(CachePolicy.DISABLED)
    // 只读
    downloadCachePolicy(CachePolicy.READ_ONLY)
    // 只写
    downloadCachePolicy(CachePolicy.WRITE_ONLY)
}
```

## 读写缓存

你可以通过 `sketch.downloadCache` 属性获取下载缓存实例来访问下载缓存，但要注意先获取锁再访问，这样能避免在多线程下出问题，如下：

```kotlin
scope.launch {
    val downloadCache = sketch.downloadCache
    val downloadCacheKey = imageRequest.downoadCacheKey
    downloadCache.withLock(downloadCacheKey) {
        // get
        openSnapshot(downloadCacheKey)?.use { snapshot ->
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
        val editor: DiskCache.Editor? = openEditor(downloadCacheKey)
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
        val cleared: Boolean = remove(downloadCacheKey)
    }

    // Clear all
    downloadCache.clear()
}
```

> [!CAUTION]
> 1. 同一个 key 的 openSnapshot 和 openEditor 是互相冲突的，例如 openSnapshot 未关闭前 openEditor
     始终返回 null，反之亦然
> 2. 所以一定要在 withLock 里面执行，否则可能会出现意外

更多可用方法请参考 [DiskCache]

## 清除缓存

下载缓存会在以下几种情况下清除：

1. 主动调用 [DiskCache] 的 `remove()`、clear()` 方法
2. 主动调用 [DiskCache].Editor 的 `abort()` 方法
3. 达到最大容量时自动清除较旧的缓存

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[DiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/DiskCache.common.kt

[LruDiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/LruDiskCache.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[HttpUriFetcher]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[CachePolicy]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/CachePolicy.kt