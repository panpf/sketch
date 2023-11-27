# BitmapPool

Translations: [简体中文](bitmap_pool_zh.md)

Android supports the reuse of Bitmaps through the BitmapFactory.Options.inBitmap field, which can
significantly reduce GC and improve before Android 8.0 The smoothness of the app

Sketch's [BitmapPool] component provides a Bitmap pool service for reusing the Bitmap feature, and
the default implementation is [LruBitmapPool]:

* Release old Bitmaps according to the principle of least use
* The maximum capacity is one-third of the lesser of 6 screen sizes and one-third of the maximum
  available memory

> You can create a [LruBitmapPool] when initializing Sketch and modify the maximum capacity, then
> register it via the bitmapPool() method

### Disable

Sketch has the Bitmap function enabled by default, and you can disable it via the
disallowReuseBitmap function of [ImageRequest] or [ImageOptions]:

```kotlin
imageView.displayImage("https://www.sample.com/image.jpg") {
    disallowReuseBitmap()
}
```

### Free

[BitmapPool] will be released in the following situations:

* Actively call the `trim()` and `clear()` methods of [BitmapPool].
* Older bitmaps are automatically released when the maximum capacity is reached
* The low available memory of the device triggers the application's `onLowMemory()` method
* The system trim memory triggers the application's `onTrimMemory(int)` method

[BitmapPool]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/cache/BitmapPool.kt

[LruBitmapPool]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/cache/internal/LruBitmapPool.kt

[ImageRequest]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageRequest.kt

[ImageOptions]: ../../sketch-core/src/main/kotlin/com/github/panpf/sketch/request/ImageOptions.kt