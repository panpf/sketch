# Cache

Sketch 为了提高图片的加载速度引入了下载缓存、Bitmap 结果缓存、Bitmap 内存缓存，而为这三种缓存提供服务的是磁盘缓存和内存缓存组件

## 磁盘缓存

磁盘缓存将图片持久的存储在磁盘上，避免重复下载或重复转换图片。

磁盘缓存由 [DiskCache] 组件提供服务，默认实现是 [LruDiskCache]：

* 根据最少使用原则清除旧的缓存
* 默认最大容量是 500MB
* 默认缓存目录是 `sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch3`，另外为了兼容多进程，当在非主进程使用 Sketch
  时缓存目录名称后会加上进程名，例如 "sketch3:push"

> 你可以在初始化 Sketch 时创建 [LruDiskCache] 并修改最大容量或缓存目录，然后通过 diskCache() 方法注册

#### 编辑

你可以通过 `sketch.diskCache` 属性获取 DiskCache 实例来访问磁盘缓存。但要注意先获取编辑锁并且上锁再访问，如下：

```kotlin
val diskCacheKey = "...."
val lock = context.diskCache.editLock(diskCacheKey)
lock.lock()
try {
    // ... 实现你的编辑逻辑
} finally {
    lock.unlock()
}
```

#### 释放

磁盘缓存会在以下几种情况下释放：

* 主动调用 DiskCache 的 remove()、clear() 方法
* 主动调用 DiskCache.Editor 的 abort() 方法
* 主动调用 DiskCache.Snapshot 的 remove() 方法
* 达到最大容量时自动释放较旧的缓存

## 内存缓存

内存缓存将 Bitmap 缓存在内存中，避免重复加载图片。

内存缓存由 [MemoryCache] 组件提供服务，默认的实现是 [LruMemoryCache]：

* 根据最少使用原则释放旧的 Bitmap
* 最大容量是 6 个屏幕大小和最大可用内存的三分之一中的小者的三分之二

> 你可以在初始化 Sketch 时创建 [LruMemoryCache] 并修改最大容量，然后通过 memoryCache() 方法注册

#### 编辑

你可以通过 `sketch.diskCache` 属性获取 DiskCache 实例来访问磁盘缓存。但要注意先获取编辑锁并且上锁再访问，如下：

```kotlin
val memoryCacheKey = "...."
val lock = context.memoryCache.editLock(memoryCacheKey)
lock.lock()
try {
    // ... 实现你的编辑逻辑
} finally {
    lock.unlock()
}
```

#### 释放

内存缓存会在以下几种情况下释放：

* 主动调用 MemoryCache 的 trim() 方法
* 缓存的 Bitmap 不再被引用
* 达到最大容量时自动释放较旧的缓存
* 设备可用内存较低触发了 Application 的 onLowMemory() 方法
* 系统整理内存触发了 Application 的 onTrimMemory(int) 方法

## 下载缓存

Sketch 默认会将 Http uri 的内容缓存到磁盘缓存，避免重复下载，以提高加载速度

你可以通过 [ImageRequest] 或 [ImageOptions] 的 downloadDiskCachePolicy 属性控制下载缓存:

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    // 禁用
    downloadDiskCachePolicy(CachePolicy.DISABLED)
    // 只读
    downloadDiskCachePolicy(CachePolicy.READ_ONLY)
    // 只写
    downloadDiskCachePolicy(CachePolicy.WRITE_ONLY)
}
```

## Bitmap 结果缓存

Sketch 默认会在以下情况将 Bitmap 缓存到磁盘缓存中，避免重复解码和转换，以提高加载速度：

* Resize 不为 null 且解码后的 Bitmap 与原图有缩小或尺寸调整
* 经过 Transformation 转换

你可以通过 [ImageRequest] 或 [ImageOptions] 的 bitmapResultDiskCachePolicy 属性控制 Bitmap 结果缓存:

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    // 禁用
    bitmapResultDiskCachePolicy(CachePolicy.DISABLED)
    // 只读
    bitmapResultDiskCachePolicy(CachePolicy.READ_ONLY)
    // 只写
    bitmapResultDiskCachePolicy(CachePolicy.WRITE_ONLY)
}
```

## Bitmap 内存缓存

Sketch 默认会将最终得到的 Bitmap 缓存到内存缓存中，避免重复加载，以提高加载速度：

你可以通过 [ImageRequest] 或 [ImageOptions] 的 bitmapMemoryCachePolicy 属性控制 Bitmap 内存缓存:

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    // 禁用
    bitmapMemoryCachePolicy(CachePolicy.DISABLED)
    // 只读
    bitmapMemoryCachePolicy(CachePolicy.READ_ONLY)
    // 只写
    bitmapMemoryCachePolicy(CachePolicy.WRITE_ONLY)
}
```

[MemoryCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/MemoryCache.kt

[LruMemoryCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/internal/LruMemoryCache.kt

[DiskCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/DiskCache.kt

[LruDiskCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/internal/LruDiskCache.kt

[ImageRequest]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch/src/main/java/com/github/panpf/sketch/request/ImageOptions.kt

[reference_article]: http://www.cnblogs.com/zhucai/p/inPreferQualityOverSpeed.html