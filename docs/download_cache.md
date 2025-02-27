# Download Cache

Translations: [简体中文](download_cache.zh.md)

In order to avoid repeatedly downloading images from the Internet and improve the loading speed of
images [Sketch] introduces download caching. [HttpUriFetcher] will first store images persistently
on the disk and then read them from the disk.

The download cache is served by the [DiskCache] component, and the default implementation
is [LruDiskCache]:

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

## Configuration request

The default configuration of the download cache is [CachePolicy].ENABLED, you can pass the downloadCachePolicy of [ImageRequest]
or [ImageOptions] Properties control download caching, as follows:

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