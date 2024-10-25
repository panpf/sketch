# Result Cache

Translations: [简体中文](result_cache_zh.md)

In order to avoid repeated conversion of images and improve the loading speed of images, [Sketch]
introduces result caching. [ResultCacheDecodeInterceptor] will persistently store the converted
images on the disk, and read them directly from the disk next time to skip the conversion process.

The result cache is served by the [DiskCache] component, and the default implementation
is [LruDiskCache]:

* Default maximum capacity is 200 MB
* Clear old cache based on least used principle

## Cache directory

In order to adapt to the differences between different platforms, the locations of cache directories
are also different on different platforms.

### Android

The default result cache directories on Android are obtained in the following order:

1. `/sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch4/result`
2. `/data/data/[APP_PACKAGE_NAME]/cache/sketch4/result`

> [!TIP]
> In order to be compatible with multiple processes, when using Sketch in a non-main process, the
> process name will be added after the cache directory name, such as "result:push"

### iOS

The default results cache directory on iOS is:

```kotlin
val appCacheDirectory =
    NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true).first() as String
val resultCacheDir = "$appCacheDirectory/sketch4/result"
```

### Desktop

The default results cache directory on desktop platforms is:

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

The web platform does not yet support result caching

## Customize

You can pass resultCache() or resultCacheOptions() of [Sketch].Builder when initializing [Sketch]
Method to customize the implementation or configuration of result cache, as follows:

```kotlin
// Use the default LruDiskCache implementation and configure its parameters
Sketch.Builder(context).apply {
    resultCacheOptions(
        DiskCache.Options(
            // Just choose one of directory and appCacheDirectory
            directory = "/tmp/myapp/sketch/result",
            // Just choose one of directory and appCacheDirectory
            appCacheDirectory = "/tmp/myapp",
            // 100 MB
            maxSize = 1024 * 1024 * 100,
            // The app's management version number for the result cache. 
            // If you want to clear the old result cache, upgrade this version number.
            appVersion = 1,
        )
    )
}.build()

// Use your own DiskCache implementation
class MyDiskCache : DiskCache {
    // ...
}
Sketch.Builder(context).apply {
    resultCache(MyDiskCache())
}.build()
```

## 配置请求

The default configuration of the result cache is [CachePolicy].ENABLED. You can control result
caching through the resultCachePolicy
attribute of [ImageRequest] or [ImageOptions], as follows:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // Disable
    resultCachePolicy(CachePolicy.DISABLED)
    // Read only
    resultCachePolicy(CachePolicy.READ_ONLY)
    // Write Only
    resultCachePolicy(CachePolicy.WRITE_ONLY)
}
```

## Read and write cache

You can access the result cache by obtaining the result cache instance through
the `sketch.resultCache` property, but be careful to obtain the lock before accessing, so as to
avoid problems under multi-threading, as follows:

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
> 1. openSnapshot and openEditor with the same key conflict with each other. For example,
     openSnapshot is not closed before openEditor is closed. Always returns null and vice versa
> 2. So it must be executed inside withLock, otherwise unexpected events may occur.

For more available methods, please refer to [DiskCache]

## Clear cache

The results cache is cleared under the following circumstances:

1. Actively call the `remove()` and clear()` methods of [DiskCache]
2. Actively call the `abort()` method of [DiskCache].Editor
3. Automatically clear older caches when maximum capacity is reached

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[DiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/DiskCache.common.kt

[LruDiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/LruDiskCache.common.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[ResultCacheDecodeInterceptor]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/ResultCacheDecodeInterceptor.kt

[CachePolicy]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/CachePolicy.kt