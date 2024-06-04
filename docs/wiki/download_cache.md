# Cache

Translations: [简体中文](download_cache_zh.md)

Sketch introduces download cache, result cache, and memory cache to improve the loading speed
of images

## Download cache

The download cache is used to store images on disk persistently to avoid duplicate downloads.

The download cache is served by the [DiskCache] component, and the default implementation
is [LruDiskCache]:

* Purge old caches based on the principle of least use
* The default maximum size is 300MB
* The default cache directory is `sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch4/download`,
  and in order to be multi-process-compatible, Sketch is used in a non-primary process
  When cache the directory name, the process name is appended, e.g. "download:push"

> You can do this by initializing Sketch via [LruDiskCache].ForDownloadBuilder creates and modifies
> the maximum capacity or cache directory, and then registers it via the downloadCache() method

#### Configure the download cache

Download cache is enabled by default, and you can control the download cache via the
downloadCachePolicy property of [ImageRequest] or [ImageOptions]:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    // Disable
    downloadCachePolicy(CachePolicy.DISABLED)
    // Read Only
    downloadCachePolicy(CachePolicy.READ_ONLY)
    // Write Only
    downloadCachePolicy(CachePolicy.WRITE_ONLY)
}
```

#### Access the download cache

You can access the download cache by getting an instance of the download cache via
the `context.sketch.downloadCache` property.

However, it is important to obtain the edit lock first and lock it before accessing it, so as to
avoid problems in multi-threading, as follows:

```kotlin
val lockKey = "http://sample.com/sample.jpeg"
val lock = context.sketch.downloadCache.editLock(lockKey)
lock.lock()
try {
    val diskCacheKey = "http://sample.com/sample.jpeg"

    // edit
    val editor: DiskCache.Editor = context.sketch.downloadCache.edit(diskCacheKey)
    try {
        editor.newOutputStream().use {
            it.write("http://sample.com/sample.jpeg".toByteArray())
        }
        editor.commit()
    } catch (e: Exception) {
        editor.abort()
    }

    // get
    val snapshot: Snapshot? = context.sketch.downloadCache.get(diskCacheKey)
    snapshot?.newInputStream().use {
        it.readBytes()
    }

    // exist
    val exist: Boolean = context.sketch.downloadCache.exist(diskCacheKey)
} finally {
    lock.unlock()
}
```

For more available methods, please refer to [DiskCache]

#### Free the download cache

The download cache is released in the following situations:

* Actively call the `remove()`, `clear()` methods of DiskCache
* Proactively call the `abort()` method of DiskCache.Editor
* The `remove()` method of DiskCache.Snapshot is actively called
* Older caches are automatically freed when maximum capacity is reached

## Result cache

The result cache is used to store the converted images on disk durably, avoiding repeated
conversions and improving loading speed.

The resulting cache is served by the [DiskCache] component, and the default implementation
is [LruDiskCache]:

* Purge old caches based on the principle of least use
* The default maximum size is 200MB
* The default cache directory is `sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch4/result`, and
  in order to be compatible with multiple processes, it should be used in non-primary processes
  When cache Sketch, the process name is appended to the directory name, e.g. "result:push"

> You can do this by initializing Sketch via [LruDiskCache]. ForResultBuilder creates and modifies
> the maximum capacity or cache directory, and then registers it via the resultCache() method

Sketch caches the Bitmap to the disk cache in the following situations:

* The resize is not null and the decoded bitmap is not the same size as the original image
* After Transformation transformation

#### Configure the result cache

Result cache is enabled by default, and you can control the bitmap result cache via the
resultCachePolicy property of [ImageRequest] or [ImageOptions]:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    // Disable
    resultCachePolicy(CachePolicy.DISABLED)
    // Read Only
    resultCachePolicy(CachePolicy.READ_ONLY)
    // Write Only
    resultCachePolicy(CachePolicy.WRITE_ONLY)
}
```

#### Access the result cache

You can access the results cache by getting the result cache instance via
the `context.sketch.resultCache` property.

However, it is important to obtain the edit lock first and lock it before accessing it, so as to
avoid problems in multi-threading, as follows:

```kotlin
val lockKey = "http://sample.com/sample.jpeg"
val lock = context.sketch.resultCache.editLock(lockKey)
lock.lock()
try {
    val diskCacheKey = "http://sample.com/sample.jpeg"

    // edit
    val editor: DiskCache.Editor = context.sketch.resultCache.edit(diskCacheKey)
    try {
        editor.newOutputStream().use {
            it.write("http://sample.com/sample.jpeg".toByteArray())
        }
        editor.commit()
    } catch (e: Exception) {
        editor.abort()
    }

    // get
    val snapshot: Snapshot? = context.sketch.resultCache.get(diskCacheKey)
    snapshot?.newInputStream().use {
        it.readBytes()
    }

    // exist
    val exist: Boolean = context.sketch.resultCache.exist(diskCacheKey)
} finally {
    lock.unlock()
}
```

For more available methods, please refer to [DiskCache]

#### Free the result cache

The result cache is released in the following situations:

* Actively call the `remove()` and `clear()` methods of DiskCache
* Actively call the `abort()` method of DiskCache.Editor
* Actively call the `remove()` method of DiskCache.Snapshot
* Automatically frees older caches when the maximum capacity is reached

## Memory cache

Memory cache is used to cache bitmaps in memory to avoid reloading images.

The memory cache is served by the [MemoryCache] component, and the default implementation
is [LruMemoryCache]:

* Release old Bitmaps according to the principle of least use
* Maximum capacity is two-thirds of the lesser of 6 screen sizes and one-third of the maximum
  available memory

> You can create a [LruMemoryCache] when initializing Sketch and modify the maximum capacity, and
> then register it via the memoryCache() method

#### Configure the memory cache

Memory cache is enabled by default, and you can control the bitmap memory cache via the
memoryCachePolicy property of [ImageRequest] or [ImageOptions]:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    // Disable
    memoryCachePolicy(CachePolicy.DISABLED)
    // Read Only
    memoryCachePolicy(CachePolicy.READ_ONLY)
    // Write Only
    memoryCachePolicy(CachePolicy.WRITE_ONLY)
}
```

#### Access the memory cache

You can access the memory cache by getting an instance of the memory cache via
the `context.sketch.memoryCache` property.

```kotlin
val memoryCacheKey = "http://sample.com/sample.jpeg"

// put
val newBitmap: Bitmap = Bitmap.create(100, 100, Bitmap.Config.ARGB_8888)
context.sketch.memoryCache.put(memoryCacheKey, MemoryCache.Value(CountBitmap(newBitmap)))

// get
val cachedBitmap: Bitmap? = context.sketch.memoryCache.get(memoryCacheKey)?.countBitmap?.bitmap

// exist
val exist: Boolean = context.sketch.memoryCache.exist(memoryCacheKey)
```

For more available methods, please refer to [MemoryCache]

#### Free the memory cache

The memory cache is released in the following situations:

* Actively call the `trim()` and `clear()` methods of MemoryCache
* Cached bitmaps are no longer referenced
* Automatically frees older caches when the maximum capacity is reached
* The low available memory of the device triggers the application's `onLowMemory()` method
* The system trim memory triggers the application's `onTrimMemory(int)` method

[MemoryCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/MemoryCache.kt

[LruMemoryCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/LruMemoryCache.kt

[DiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/DiskCache.kt

[LruDiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/LruDiskCache.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[reference_article]: http://www.cnblogs.com/zhucai/p/inPreferQualityOverSpeed.html