# Memory Cache

Translations: [简体中文](memory_cache_zh.md)

In order to avoid repeated loading of images and improve the loading speed of images, Sketch
introduces memory caching. [MemoryCacheRequestInterceptor] will cache the loaded Image in the memory
and read it directly from the memory next time to skip the loading process.

The memory cache is served by the [MemoryCache] component, and the default implementation
is [LruMemoryCache]:

* Release the old Bitmap based on the least used principle
* Maximum capacity is 25% to 33% of maximum available memory on Android and 15% of maximum available
  memory on non-Android

## Customize

You can customize the implementation or configuration of the memory cache through the memoryCache()
method of [Sketch].Builder when initializing [Sketch], as follows:

```kotlin
// Use the default MemoryCache implementation and configure its parameters
Sketch.Builder(context).apply {
    memoryCache(
        MemoryCache.Builder(context)
            .maxSizePercent(0.4f)
            .build()
    )
}.build()

// Use your own MemoryCache implementation
class MyMemoryCache : MemoryCache {
    // ...
}
Sketch.Builder(context).apply {
    memoryCache(MyDiskCache())
}.build()
```

## Configuration request

The default configuration of the memory cache is [CachePolicy].ENABLED. You can control memory
caching through the memoryCachePolicy
attribute of [ImageRequest] or [ImageOptions]:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // Disable
    memoryCachePolicy(CachePolicy.DISABLED)
    // Read only
    memoryCachePolicy(CachePolicy.READ_ONLY)
    // Write Only
    memoryCachePolicy(CachePolicy.WRITE_ONLY)
}
```

## Read and write cache

You can access the memory cache by getting the memory cache instance through
the `sketch.memoryCache` property.

```kotlin
scope.launch {
    val memoryCache = sketch.memoryCache
    val memoryCacheKey = requestContext.memoryCacheKey
    memoryCache.withLock(memoryCacheKey) {
        // put
        val newBitmap: Bitmap = Bitmap.create(100, 100, Bitmap.Config.ARGB_8888)
        val newCacheValue = newBitmap.asSketchImage().cacheValue()!!
        put(memoryCacheKey, newCacheValue)

        // exist
        val exist: Boolean = exist(memoryCacheKey)

        // get
        val cachedValue: MemoryCache.Value? = get(memoryCacheKey)
        val image: Image = cachedValue?.image

        // remove
        val clearedValue: MemoryCache.Value? = remove(memoryCacheKey)
    }

    // Clear all
    memoryCache.clear()

    // trim
    memoryCache.trim((memoryCache.maxSize * 0.5f).toLong())
}
```

> [!CAUTION]
> When accessing the memoryCache of a specified key, you must first obtain the lock and then access
> it. This will not only avoid repeated loading of the same request, but also avoid problems under
> multi-threading.

For more available methods, please refer to [MemoryCache]

## Clear cache

The memory cache is cleared under the following circumstances:

* Actively call the `trim()`, `remove()`, and `clear()` methods of MemoryCache
* Automatically release older caches when maximum capacity is reached
* The low available memory of the device triggers the `onLowMemory()` method of Application
* System trimming memory triggers Application's `onTrimMemory(int)` method

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[MemoryCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/MemoryCache.kt

[LruMemoryCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/LruMemoryCache.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[MemoryCacheRequestInterceptor]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/MemoryCacheRequestInterceptor.kt

[CachePolicy]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/CachePolicy.kt