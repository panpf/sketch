# Cache

Sketch 为了提高图片的加载速度引入了下载缓存、Bitmap 结果缓存、Bitmap 内存缓存，而为这三种缓存提供服务的是磁盘缓存和内存缓存组件

## 磁盘缓存

磁盘缓存将图片持久的存储在磁盘上，避免重复下载或重复转换图片。

磁盘缓存由 [DiskCache] 组件提供服务，默认实现是 [LruDiskCache]：

* 根据最少使用原则清除旧的缓存
* 默认最大容量是 500MB
* 默认缓存目录是 `sdcard/Android/data/[APP_PACKAGE_NAME]/cache/sketch3`，另外为了兼容多进程，当在非主进程使用 [Sketch]
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

## Bitmap 结果缓存# 使用 cacheProcessedImageInDisk 属性缓存需要复杂处理的图片，提升显示速度

一张图片要经过读取（或缩略图模式读取）和 ImageProcessor 处理才能显示在页面上，这两步通常也是整个显示过程中最耗时的，为了加快显示速度，减少用户等待时间我们可以将最终经过
inSampleSize 缩小的、缩略图模式读取的或 [ImageProcessor] 处理过的图片缓存在磁盘中，下次就可以读取后直接使用

### 如何开启：

```java
DisplayOptions displayOptions=new DisplayOptions();

        displayOptions.setCacheProcessedImageInDisk(true);
```

### 使用条件

并不是你只要开启了就一定会将最终的图片缓存在磁盘缓存中，还需要满足以下任一条件：

* 有 maxSize 并且最终计算得出的 inSampleSize 大于等于 8
* 有 resize
* 有 ImageProcessor，并且确实生成了一张新的图片
* thumbnailMode 为 true 并且 resize 不为 null

### 存在的问题

由于 Android 天然存在的
BUG，导致读到内存里的图片，再保存到磁盘后图片会发生轻微的色彩变化（通常是发黄并丢失一些细节），因此在使用此功能时还是要慎重考虑此因素带来的影响，参考文章 [Android 中 decode JPG 时建议使用 inPreferQualityOverSpeed][reference_article]

`此功能读取图片时已强制设置 inPreferQualityOverSpeed 为 true`

## Bitmap 内存缓存

[MemoryCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/MemoryCache.kt

[LruMemoryCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/internal/LruMemoryCache.kt

[Sketch]: ../../sketch/src/main/java/com/github/panpf/sketch/Sketch.kt

[DiskCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/DiskCache.kt

[LruDiskCache]: ../../sketch/src/main/java/com/github/panpf/sketch/cache/internal/LruDiskCache.kt

[reference_article]: http://www.cnblogs.com/zhucai/p/inPreferQualityOverSpeed.html