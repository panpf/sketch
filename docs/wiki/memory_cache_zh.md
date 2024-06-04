# Cache

[//]: # (TODO)

翻译：[English](memory_cache.md)

Sketch 为了提高图片的加载速度引入了下载缓存、结果缓存、内存缓存

## 下载缓存

下载缓存用于将图片持久的存储在磁盘上，避免重复下载图片。

下载缓存由 [DiskCache] 组件提供服务，默认实现是 [LruDiskCache]：

* 根据最少使用原则清除旧的缓存
* 默认最大容量是 300MB
* 默认缓存目录是 `sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch4/download`，另外为了兼容多进程，当在非主进程使用
  Sketch
  时缓存目录名称后会加上进程名，例如 "download:push"

> 你可以在初始化 Sketch 时通过 [LruDiskCache].ForDownloadBuilder 创建并修改最大容量或缓存目录，然后通过
> downloadCache() 方法注册

#### 配置下载缓存

下载缓存默认开启，你可以通过 [ImageRequest] 或 [ImageOptions] 的 downloadCachePolicy
属性控制下载缓存:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    // 禁用
    downloadCachePolicy(CachePolicy.DISABLED)
    // 只读
    downloadCachePolicy(CachePolicy.READ_ONLY)
    // 只写
    downloadCachePolicy(CachePolicy.WRITE_ONLY)
}
```

#### 访问下载缓存

你可以通过 `context.sketch.downloadCache` 属性获取下载缓存实例来访问下载缓存。

但要注意先获取编辑锁并且上锁再访问，这样能避免在多线程下出问题，如下：

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

更多可用方法请参考 [DiskCache]

#### 释放下载缓存

下载缓存会在以下几种情况下释放：

* 主动调用 DiskCache 的 `remove()`、clear()` 方法
* 主动调用 DiskCache.Editor 的 `abort()` 方法
* 主动调用 DiskCache.Snapshot 的 `remove()` 方法
* 达到最大容量时自动释放较旧的缓存

## 结果缓存

结果缓存用于将转换后的图片持久的存储在磁盘上，避免重复转换，提高加载速度。

结果缓存由 [DiskCache] 组件提供服务，默认实现是 [LruDiskCache]：

* 根据最少使用原则清除旧的缓存
* 默认最大容量是 200MB
* 默认缓存目录是 `sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch4/result`，另外为了兼容多进程，当在非主进程使用
  Sketch
  时缓存目录名称后会加上进程名，例如 "result:push"

> 你可以在初始化 Sketch 时通过 [LruDiskCache].ForResultBuilder 创建并修改最大容量或缓存目录，然后通过
> resultCache() 方法注册

Sketch 会在以下情况将 Bitmap 缓存到磁盘缓存中：

* Resize 不为 null 且解码后的 Bitmap 与原图尺寸不一样
* 经过 Transformation 转换

#### 配置结果缓存

结果缓存默认开启，你可以通过 [ImageRequest] 或 [ImageOptions] 的 resultCachePolicy 属性控制 Bitmap
结果缓存:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    // 禁用
    resultCachePolicy(CachePolicy.DISABLED)
    // 只读
    resultCachePolicy(CachePolicy.READ_ONLY)
    // 只写
    resultCachePolicy(CachePolicy.WRITE_ONLY)
}
```

#### 访问结果缓存

你可以通过 `context.sketch.resultCache` 属性获取结果缓存实例来访问结果缓存。

但要注意先获取编辑锁并且上锁再访问，这样能避免在多线程下出问题，如下：

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

更多可用方法请参考 [DiskCache]

#### 释放结果缓存

结果缓存会在以下几种情况下释放：

* 主动调用 DiskCache 的 `remove()`、`clear()` 方法
* 主动调用 DiskCache.Editor 的 `abort()` 方法
* 主动调用 DiskCache.Snapshot 的 `remove()` 方法
* 达到最大容量时自动释放较旧的缓存

## 内存缓存

内存缓存用于将 Bitmap 缓存在内存中，避免重复加载图片。

内存缓存由 [MemoryCache] 组件提供服务，默认的实现是 [LruMemoryCache]：

* 根据最少使用原则释放旧的 Bitmap
* 最大容量是 6 个屏幕大小和最大可用内存的三分之一中的小者的三分之二

> 你可以在初始化 Sketch 时创建 [LruMemoryCache] 并修改最大容量，然后通过 memoryCache() 方法注册

#### 配置内存缓存

内存缓存默认开启，你可以通过 [ImageRequest] 或 [ImageOptions] 的 memoryCachePolicy 属性控制 Bitmap
内存缓存:

```kotlin
imageView.displayImage("https://example.com/image.jpg") {
    // 禁用
    memoryCachePolicy(CachePolicy.DISABLED)
    // 只读
    memoryCachePolicy(CachePolicy.READ_ONLY)
    // 只写
    memoryCachePolicy(CachePolicy.WRITE_ONLY)
}
```

#### 访问内存缓存

你可以通过 `context.sketch.memoryCache` 属性获取内存缓存实例来访问内存缓存。

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

更多可用方法请参考 [MemoryCache]

#### 释放内存缓存

内存缓存会在以下几种情况下释放：

* 主动调用 MemoryCache 的 `trim()`、`clear()` 方法
* 缓存的 Bitmap 不再被引用
* 达到最大容量时自动释放较旧的缓存
* 设备可用内存较低触发了 Application 的 `onLowMemory()` 方法
* 系统整理内存触发了 Application 的 `onTrimMemory(int)` 方法

[MemoryCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/MemoryCache.kt

[LruMemoryCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/LruMemoryCache.kt

[DiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/DiskCache.kt

[LruDiskCache]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/cache/internal/LruDiskCache.kt

[ImageRequest]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/commonMain/kotlin/com/github/panpf/sketch/request/ImageOptions.kt

[reference_article]: http://www.cnblogs.com/zhucai/p/inPreferQualityOverSpeed.html