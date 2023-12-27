# BitmapPool

翻译：[English](bitmap_pool.md)

Android 支持通过 BitmapFactory.Options.inBitmap 字段来复用 Bitmap，复用 Bitmap 可以在 Android 8.0
以下显著的减少 GC 进而提高
App 的流畅度

Sketch 的 [BitmapPool] 组件为复用 Bitmap 功能提供了 Bitmap 池服务，默认的实现是 [LruBitmapPool]：

* 根据最少使用原则释放旧的 Bitmap
* 最大容量是 6 个屏幕大小和最大可用内存的三分之一中的小者的三分之一

> 你可以在初始化 Sketch 时创建 [LruBitmapPool] 并修改最大容量，然后通过 bitmapPool() 方法注册

### 禁用

Sketch 默认开启了复用 Bitmap 功能，你可以通过 [ImageRequest] 或 [ImageOptions] 的 disallowReuseBitmap
函数禁用它:

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    disallowReuseBitmap()
}
```

### 释放

[BitmapPool] 会在以下几种情况下释放：

* 主动调用 [BitmapPool] 的 `trim()`、`clear()` 方法
* 达到最大容量时自动释放较旧的 Bitmap
* 设备可用内存较低触发了 Application 的 `onLowMemory()` 方法
* 系统整理内存触发了 Application 的 `onTrimMemory(int)` 方法

[BitmapPool]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/cache/BitmapPool.kt

[LruBitmapPool]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/cache/internal/LruBitmapPool.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageOptions.kt