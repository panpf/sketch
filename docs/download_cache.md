# Download Cache

Translations: [简体中文](download_cache.zh.md)

In order to avoid repeatedly downloading pictures from the network and improve the loading speed of
pictures, Sketch has introduced download cache. The download cache function will first store the
pictures on disk for a long time, then read them from disk, and skip the download process next time
you read them directly from disk.

[HttpUriFetcher] is responsible for the core logic, and [DiskCache] is responsible for the storage
management.

The default implementation of [DiskCache] is [LruDiskCache]:

* The default maximum capacity is 300 MB
* Clear old cache based on least used principle

## Cache Directory

In order to adapt to the differences between different platforms, the locations of cache directories
are also different on different platforms.

### Android

The default download cache directory on Android is obtained in the following order:

1. `/sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch4/download`
2. `/data/data/[APP_PACKAGE_NAME]/cache/sketch4/download`

> [!TIP]
> In order to be compatible with multiple processes, when using Sketch in a non-main process, the
> process name will be added after the cache directory name, such as "download:push"

### iOS

The default download cache directory on iOS is:

```kotlin
val appCacheDirectory =
    NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true).first() as String
val downloadCacheDir = "$appCacheDirectory/sketch4/download"
```

### Desktop

The default download cache directory on desktop platforms is:

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

The web platform does not yet support download caching

## Customize

You can pass [Sketch].Builder's downloadCache() or downloadCacheOptions() when initializing [Sketch]
Method to customize the implementation or configuration of download cache, as follows:

```kotlin
// Use the default LruDiskCache implementation and configure its parameters
Sketch.Builder(context).apply {
    downloadCacheOptions(
        DiskCache.Options(
            // Just choose one of directory and appCacheDirectory
            directory = "/tmp/myapp/sketch/download",
            // Just choose one of directory and appCacheDirectory
            appCacheDirectory = "/tmp/myapp",
            // 100 MB
            maxSize = 1024 * 1024 * 100,
            // The app's management version number for the download cache. 
            // If you want to clear the old download cache, upgrade this version number.
            appVersion = 1,
        )
    )
}.build()

// Use your own DiskCache implementation
class MyDiskCache : DiskCache {
    // ...
}
Sketch.Builder(context).apply {
    downloadCache(MyDiskCache())
}.build()
```

## Cache Policy

The download cache policy is used to control how to use the download cache. The default
configuration is [CachePolicy].ENABLED, which you can configure via the downloadCachePolicy property
of [ImageRequest] or [ImageOptions]:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // Disable
    downloadCachePolicy(CachePolicy.DISABLED)
    // Read only
    downloadCachePolicy(CachePolicy.READ_ONLY)
    // Write Only
    downloadCachePolicy(CachePolicy.WRITE_ONLY)
}
```

## Cache key

By default, Sketch will automatically generate a download cache key based on the requested
configuration, but you can also customize the download cache key with the following properties:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
     // Use custom download cache key
     downloadCacheKey("https://example.com/image.jpg?width=100&height=100")

     // Modify the automatically generated download cache key
     downloadCacheKeyMapper(CacheKeyMapper { "${it}&width=100&height=100" })
}

ImageOptions {
     // Use custom download cache key
     downloadCacheKey("https://example.com/image.jpg?width=100&height=100")

     // Modify the automatically generated download cache key
     downloadCacheKeyMapper(CacheKeyMapper { "${it}&width=100&height=100" })
}
```

You can also get the final download cache key through the following methods:

```kotlin
// The download cache key can be obtained through RequestContext in the customized RequestInterceptor, 
// DecodeInterceptor, Transformation, Fetcher, and Decoder components.
val requestContext: RequestContext = ...
requestContext.downloadCacheKey

// Get the download cache key from ImageResult
val imageSuccess = sketch.execute(request) as ImageResult.Success
imageSuccess.downloadCacheKey
```

## Read and write cache

You can obtain the download cache instance through the `sketch.downloadCache` property to access the
download cache, but be careful to obtain the lock first before accessing, so as to avoid problems
under multi-threading, as follows:

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
> 1. openSnapshot and openEditor with the same key conflict with each other. For example,
     openSnapshot is not closed before openEditor is closed. Always returns null and vice versa
> 2. So it must be executed inside withLock, otherwise unexpected events may occur.

For more available methods, please refer to [DiskCache]

## Clear cache

The download cache is cleared under the following circumstances:

1. Actively call the `remove()` and clear()` methods of [DiskCache]
2. Actively call the `abort()` method of [DiskCache].Editor
3. Automatically clear older caches when maximum capacity is reached

[Sketch]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[DiskCache]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/DiskCache.common.kt

[LruDiskCache]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/LruDiskCache.common.kt

[ImageRequest]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[HttpUriFetcher]: ../sketch-http-core/src/commonMain/kotlin/com/github/panpf/sketch/fetch/HttpUriFetcher.kt

[CachePolicy]: ../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/CachePolicy.kt