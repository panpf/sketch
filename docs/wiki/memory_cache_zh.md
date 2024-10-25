# 内存缓存

翻译：[English](memory_cache.md)

为了避免重复加载图片并提高图片的加载速度 Sketch 引入了内存缓存，[MemoryCacheRequestInterceptor]
会将已加载的 Image 缓存在内存中，下次直接从内存中读取跳过加载过程。

内存缓存由 [MemoryCache] 组件提供服务，默认的实现是 [LruMemoryCache]：

* 根据最少使用原则释放旧的 Bitmap
* 最大容量在 Android 上是最大可用内存的 25% 到 33%，在非 Android 上是最大可用内存的 15%

## 自定义

你可以在初始化 [Sketch] 时通过 [Sketch].Builder 的 memoryCache() 方法自定义内存缓存的实现或配置，如下：

```kotlin
// 使用默认的 MemoryCache 实现并配置其参数
Sketch.Builder(context).apply {
    memoryCache(
        MemoryCache.Builder(context)
            .maxSizePercent(0.4f)
            .build()
    )
}.build()

// 使用你自己的 MemoryCache 实现
class MyMemoryCache : MemoryCache {
    // ...
}
Sketch.Builder(context).apply {
    memoryCache(MyDiskCache())
}.build()
```

## 配置请求

内存缓存默认配置是 [CachePolicy].ENABLED，你可以通过 [ImageRequest] 或 [ImageOptions] 的 memoryCachePolicy 属性控制内存缓存:

```kotlin
ImageRequest(context, "https://example.com/image.jpg") {
    // 禁用
    memoryCachePolicy(CachePolicy.DISABLED)
    // 只读
    memoryCachePolicy(CachePolicy.READ_ONLY)
    // 只写
    memoryCachePolicy(CachePolicy.WRITE_ONLY)
}
```

## 读写缓存

你可以通过 `sketch.memoryCache` 属性获取内存缓存实例来访问内存缓存。

```kotlin
scope.launch {
    val memoryCache = sketch.memoryCache
    val memoryCacheKey = requestContext.memoryCacheKey
    memoryCache.withLock(memoryCacheKey) {
        // put
        val newBitmap: Bitmap = Bitmap.create(100, 100, Bitmap.Config.ARGB_8888)
        val newCacheValue = newBitmap.asImage().cacheValue()!!
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
> 访问指定 key 的 memoryCache 时也要先获取锁再访问，这样不仅能避免同一个请求重复加载，也能避免在多线程下出问题

更多可用方法请参考 [MemoryCache]

## 清除缓存

内存缓存会在以下几种情况下清除：

* 主动调用 MemoryCache 的 `trim()`、`remove()`、`clear()` 方法
* 达到最大容量时自动释放较旧的缓存
* 设备可用内存较低触发了 Application 的 `onLowMemory()` 方法
* 系统整理内存触发了 Application 的 `onTrimMemory(int)` 方法

[Sketch]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/Sketch.common.kt

[MemoryCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/MemoryCache.common.kt

[LruMemoryCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/LruMemoryCache.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.common.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.common.kt

[MemoryCacheRequestInterceptor]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/MemoryCacheRequestInterceptor.kt

[CachePolicy]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/CachePolicy.kt